package org.example.poc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Attendance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AttendanceSet {
    private Integer id;
    @NotNull(message = "Không được để ngày làm việc trống")
    private LocalDate workDate;
    @NotNull(message = "Không được để giờ check-in trống")
    private LocalTime checkIn;
    @NotNull(message = "Không được để giờ check-out trống")
    private LocalTime checkOut;
    @NotNull(message = "Không được để số giờ làm việc trống")
    private BigDecimal workingHours;

    public Attendance dto(Attendance attendance) {
        attendance.setId(this.getId());
        attendance.setWorkDate(this.getWorkDate());
        attendance.setCheckIn(this.getCheckIn());
        attendance.setCheckOut(this.getCheckOut());
        attendance.setWorkingHours(this.getWorkingHours());
        return attendance;
    }
}
