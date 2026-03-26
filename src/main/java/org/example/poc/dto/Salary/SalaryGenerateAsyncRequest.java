package org.example.poc.dto.Salary;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryGenerateAsyncRequest {
    @NotNull(message = "Month must not be empty")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year must not be empty")
    @Min(value = 2000, message = "Year is invalid")
    @Max(value = 3000, message = "Year is invalid")
    private Integer year;

    @NotNull(message = "overwriteDraft must not be empty")
    private Boolean overwriteDraft;
}
