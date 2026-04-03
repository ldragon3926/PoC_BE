# Kafka + Docker + Kafka Connect Integration Guide

Tài liệu này tích hợp trực tiếp các yêu cầu:

- Quy tắc Docker build tag và version.
- Kafka Dead Letter Queue (DLQ) set thủ công trong consumer.
- Partition, offset (index) và quy ước key.
- Kafka Connect với raw query nhiều `JOIN`.
- Schema khi đẩy dữ liệu qua Kafka Connect.

## 1. Docker build tag và version

Script đã thêm: `scripts/docker-build.ps1`

Ví dụ:

```powershell
.\scripts\docker-build.ps1 -ImageName poc-app -Version 1.3.0 -TagLatest
```

Kết quả tạo 2-3 tag:

- `poc-app:1.3.0`
- `poc-app:1.3.0-<gitsha>`
- `poc-app:latest` (nếu bật `-TagLatest`)

Quy tắc khuyến nghị:

- `MAJOR.MINOR.PATCH` theo SemVer.
- `MAJOR`: breaking change.
- `MINOR`: thêm tính năng, tương thích ngược.
- `PATCH`: sửa lỗi, không đổi hành vi API.

## 2. Kafka DLQ set thủ công

Đã tích hợp trong app:

- Topic chính: `app.kafka.topics.salary-generate-requested`
- Topic DLQ: `app.kafka.topics.salary-generate-requested-dlq`
- Class publish DLQ: `SalaryJobDlqProducer`
- Metadata DLQ payload:
  - original topic/partition/offset/key
  - error type/message
  - failed timestamp
  - payload gốc

Luồng xử lý:

1. Consumer nhận message từ topic chính.
2. Gọi `salaryJobService.processGenerateMonthJob(...)`.
3. Nếu fail runtime, message được publish thủ công sang DLQ topic.
4. Consumer không rethrow để tránh loop retry + duplicate DLQ.

## 3. Partition, offset (index), key

Config mới:

- `APP_KAFKA_TOPIC_PARTITIONS` (mặc định `3`)
- `APP_KAFKA_TOPIC_REPLICAS` (mặc định `1`)

Giải thích:

- `partition`: đơn vị song song hóa consume/produce.
- `offset` (thường bị gọi nhầm là index): vị trí message trong mỗi partition.
- `key`: quyết định route partition (cùng key thường vào cùng partition).

Trong app hiện tại, key đang dùng `jobKey` nên đảm bảo order theo từng job key.

## 4. Kafka Connect + raw query nhiều JOIN

Đã thêm file:

- `scripts/sql/kafka_connect_prepare.sql`
  - tạo index cho cột join/filter
  - tạo view `vw_salary_enriched_export`
- `kafka-connect/connectors/salary-raw-source.json`
  - JDBC Source đọc từ view join nhiều bảng
  - mode `incrementing` theo `attendance_id`
  - có DLQ cho Connect lỗi parse/transform
- `kafka-connect/connectors/salary-raw-sink.json`
  - JDBC Sink mẫu để ghi về DB

View `vw_salary_enriched_export` join nhiều bảng:

- `attendance`, `employees`, `departments`, `users`, `user_roles`, `roles`, `role_permissions`, `permissions`, `contracts`, `rewards`.

## 5. Schema kết hợp Kafka Connect

Trong connector config đang bật:

- `value.converter=org.apache.kafka.connect.json.JsonConverter`
- `value.converter.schemas.enable=true`

Điều này giúp message có schema kèm payload (schema-aware JSON).
Nếu muốn schema governance mạnh hơn, có thể chuyển qua Avro/Protobuf + Schema Registry.

Đã thêm compose mẫu:

- `kafka-connect/docker-compose.kafka-connect.yml`
  - chạy `kafka-connect` REST ở `:8083`
  - schema registry ở `:8081`

## 6. Test dữ liệu lớn

Đã thêm script:

- `scripts/sql/generate_attendance_test_data.sql`

Ví dụ tạo dữ liệu:

```sql
CALL seed_attendance(3650);
```

Gợi ý test:

1. Chạy seed dữ liệu lớn.
2. `EXPLAIN` query/view trước và sau khi có index.
3. Quan sát độ trễ connector (`poll.interval.ms`, `batch.max.rows`).
4. Kiểm tra DLQ topic:
   - `salary.generate.requested.dlq` (app runtime)
   - `salary.connect.dlq` / `salary.connect.sink.dlq` (connect runtime)

## 7. Lệnh chạy nhanh

Chạy Kafka broker:

```powershell
docker compose -f .\docker-compose.kafka.yml up -d
```

Chạy Kafka Connect + Schema Registry:

```powershell
docker compose -f .\kafka-connect\docker-compose.kafka-connect.yml up -d
```

Đăng ký source connector:

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8083/connectors `
  -ContentType "application/json" `
  -InFile .\kafka-connect\connectors\salary-raw-source.json
```

Đăng ký sink connector:

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8083/connectors `
  -ContentType "application/json" `
  -InFile .\kafka-connect\connectors\salary-raw-sink.json
```

Xem connector status:

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8083/connectors/salary-raw-source/status
```
