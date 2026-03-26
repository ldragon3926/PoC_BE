package org.example.poc.service.impl;

import org.example.poc.entity.Contract;
import org.example.poc.entity.Salary;
import org.example.poc.entity.SalaryStatus;
import org.example.poc.repository.AttendanceRepository;
import org.example.poc.repository.ContractRepository;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.repository.RewardRepository;
import org.example.poc.repository.SalaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    private SalaryServiceImpl salaryService;

    @BeforeEach
    void setUp() {
        salaryService = new SalaryServiceImpl(
                salaryRepository,
                attendanceRepository,
                contractRepository,
                rewardRepository,
                employeeRepository
        );
    }

    @Test
    void generateMonthFromAttendance_shouldOverwriteDraft_whenOverwriteDraftIsTrue() {
        AttendanceRepository.AttendanceWorkdaySummary summary = summary(1, 15L);
        Salary existing = new Salary();
        existing.setStatus(SalaryStatus.DRAFT);
        existing.setBaseSalary(new BigDecimal("999.00"));
        existing.setAllowance(new BigDecimal("0.00"));
        existing.setDeduction(new BigDecimal("0.00"));
        existing.setTotalSalary(new BigDecimal("999.00"));

        when(attendanceRepository.summarizeWorkedDaysByEmployeeInMonth(3, 2026))
                .thenReturn(List.of(summary));
        when(contractRepository.findLatestContractByEmployeeId(1, org.springframework.data.domain.PageRequest.of(0, 1)))
                .thenReturn(List.of(contract("3000.00", "1.00")));
        when(rewardRepository.sumRewardByEmployeeInMonth(1, 3, 2026))
                .thenReturn(new BigDecimal("200.00"));
        when(salaryRepository.findByEmployee_IdAndMonthAndYear(1, 3, 2026))
                .thenReturn(Optional.of(existing));
        when(salaryRepository.save(any(Salary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int changed = salaryService.generateMonthFromAttendance(3, 2026, true);

        assertEquals(1, changed);
        assertEquals(0, new BigDecimal("1451.61").compareTo(existing.getBaseSalary()));
        assertEquals(0, new BigDecimal("200.00").compareTo(existing.getAllowance()));
        assertEquals(0, BigDecimal.ZERO.compareTo(existing.getDeduction()));
        assertEquals(0, new BigDecimal("1651.61").compareTo(existing.getTotalSalary()));
        verify(salaryRepository).save(existing);
    }

    @Test
    void generateMonthFromAttendance_shouldSkipDraft_whenOverwriteDraftIsFalse() {
        AttendanceRepository.AttendanceWorkdaySummary summary = summary(1, 15L);
        Salary existing = new Salary();
        existing.setStatus(SalaryStatus.DRAFT);
        existing.setBaseSalary(new BigDecimal("999.00"));
        existing.setAllowance(new BigDecimal("0.00"));
        existing.setDeduction(new BigDecimal("0.00"));
        existing.setTotalSalary(new BigDecimal("999.00"));

        when(attendanceRepository.summarizeWorkedDaysByEmployeeInMonth(3, 2026))
                .thenReturn(List.of(summary));
        when(contractRepository.findLatestContractByEmployeeId(1, org.springframework.data.domain.PageRequest.of(0, 1)))
                .thenReturn(List.of(contract("3000.00", "1.00")));
        when(rewardRepository.sumRewardByEmployeeInMonth(1, 3, 2026))
                .thenReturn(new BigDecimal("200.00"));
        when(salaryRepository.findByEmployee_IdAndMonthAndYear(1, 3, 2026))
                .thenReturn(Optional.of(existing));

        int changed = salaryService.generateMonthFromAttendance(3, 2026, false);

        assertEquals(0, changed);
        assertEquals(0, new BigDecimal("999.00").compareTo(existing.getBaseSalary()));
        verify(salaryRepository, never()).save(any(Salary.class));
    }

    private AttendanceRepository.AttendanceWorkdaySummary summary(Integer employeeId, Long workedDays) {
        return new AttendanceRepository.AttendanceWorkdaySummary() {
            @Override
            public Integer getEmployeeId() {
                return employeeId;
            }

            @Override
            public Long getWorkedDays() {
                return workedDays;
            }
        };
    }

    private Contract contract(String baseSalary, String coefficient) {
        Contract contract = new Contract();
        contract.setBaseSalary(new BigDecimal(baseSalary));
        contract.setSalaryCoefficient(new BigDecimal(coefficient));
        return contract;
    }

}
