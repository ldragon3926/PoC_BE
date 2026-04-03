package org.example.poc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IntegrationMonitorService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${app.integration.kafka-connect-url:http://localhost:8083}")
    private String kafkaConnectUrl;

    @Value("${app.integration.schema-registry-url:http://localhost:8081}")
    private String schemaRegistryUrl;

    public IntegrationMonitorService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();
    }

    public Map<String, Object> getOverview() {
        boolean connectReachable = isHealthy(kafkaConnectUrl + "/connectors");
        boolean schemaRegistryReachable = isHealthy(schemaRegistryUrl + "/subjects");
        List<Map<String, Object>> connectors = new ArrayList<>();
        connectors.add(getConnectorStatus("salary-raw-source"));
        connectors.add(getConnectorStatus("salary-raw-sink"));

        return Map.of(
                "kafkaConnectUrl", kafkaConnectUrl,
                "schemaRegistryUrl", schemaRegistryUrl,
                "kafkaConnectReachable", connectReachable,
                "schemaRegistryReachable", schemaRegistryReachable,
                "connectors", connectors
        );
    }

    public Map<String, Object> getConnectorStatus(String connectorName) {
        String url = kafkaConnectUrl + "/connectors/" + connectorName + "/status";
        try {
            JsonNode root = getJson(url);
            List<Map<String, Object>> tasks = new ArrayList<>();
            JsonNode tasksNode = root.path("tasks");
            if (tasksNode.isArray()) {
                for (JsonNode task : tasksNode) {
                    tasks.add(Map.of(
                            "id", task.path("id").asInt(-1),
                            "state", task.path("state").asText("UNKNOWN"),
                            "workerId", task.path("worker_id").asText(""),
                            "trace", task.path("trace").asText("")
                    ));
                }
            }
            return Map.of(
                    "name", root.path("name").asText(connectorName),
                    "type", root.path("type").asText(""),
                    "state", root.path("connector").path("state").asText("UNKNOWN"),
                    "workerId", root.path("connector").path("worker_id").asText(""),
                    "tasks", tasks
            );
        } catch (Exception ex) {
            return Map.of(
                    "name", connectorName,
                    "type", "",
                    "state", "UNREACHABLE",
                    "workerId", "",
                    "tasks", List.of(),
                    "error", ex.getMessage()
            );
        }
    }

    private boolean isHealthy(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception ex) {
            return false;
        }
    }

    private JsonNode getJson(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Call failed: " + url + " with status " + response.statusCode());
            }
            return objectMapper.readTree(response.body());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot call " + url + ": " + ex.getMessage());
        }
    }
}
