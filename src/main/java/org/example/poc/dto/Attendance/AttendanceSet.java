package org.example.poc.dto.Attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Attendance;
import org.example.poc.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AttendanceSet {
    private Integer id;

    @NotNull(message = "Khong duoc de nhan vien trong")
    private Integer employeeId;

    @NotNull(message = "Khong duoc de ngay lam viec trong")
    private LocalDate workDate;

    @NotNull(message = "Khong duoc de gio check-in trong")
    private LocalTime checkIn;

    @NotNull(message = "Khong duoc de gio check-out trong")
    private LocalTime checkOut;

    @NotNull(message = "Khong duoc de so gio lam viec trong")
    private BigDecimal workingHours;

    public Attendance dto(Attendance attendance, Employee employee) {
        attendance.setId(this.getId());
        attendance.setEmployee(employee);
        attendance.setWorkDate(this.getWorkDate());
        attendance.setCheckIn(this.getCheckIn());
        attendance.setCheckOut(this.getCheckOut());
        attendance.setWorkingHours(this.getWorkingHours());
        return attendance;
    }
}
