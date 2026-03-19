package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Reward;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class RewardSet {
    private Integer id;
    @NotNull(message = "Không được để nhân viên trống")
    private Integer employeeId;
    @NotNull(message = "Không được để số tiền thưởng trống")
    private BigDecimal amount;
    @NotBlank(message = "Không được để lý do thưởng trống")
    private String reason;
    private Instant createdAt;

    public Reward dto(Reward reward, Employee employee) {
        reward.setId(this.getId());
        reward.setEmployee(employee);
        reward.setAmount(this.getAmount());
        reward.setReason(this.getReason());
        reward.setCreatedAt(this.getCreatedAt());
        return reward;
    }
}
