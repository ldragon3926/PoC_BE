package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Contract;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Salary;
import org.example.poc.entity.SalaryStatus;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.AttendanceRepository;
import org.example.poc.repository.ContractRepository;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.repository.RewardRepository;
import org.example.poc.repository.SalaryRepository;
import org.example.poc.service.SalaryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final AttendanceRepository attendanceRepository;
    private final ContractRepository contractRepository;
    private final RewardRepository rewardRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Salary> findAll() {
        return salaryRepository.findAllWithEmployee();
    }

    @Override
    public List<Salary> findAllFiltered(String keyword, Integer employeeId, Integer month, Integer year, SalaryStatus status) {
        String normalizedKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        return salaryRepository.findAllWithFilters(normalizedKeyword, employeeId, month, year, status);
    }

    @Override
    public Optional<Salary> findById(Integer id) {
        return salaryRepository.findByIdWithEmployee(id);
    }

    @Override
    public Salary getOne(Integer id) {
        return salaryRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find salary with id: " + id));
    }

    @Override
    @Transactional
    public Salary add(Salary salary) {
        Integer employeeId = salary.getEmployee() == null ? null : salary.getEmployee().getId();
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee is required");
        }
        if (salaryRepository.existsByEmployee_IdAndMonthAndYear(employeeId, salary.getMonth(), salary.getYear())) {
            throw new IllegalArgumentException("Salary for this employee and month/year already exists");
        }
        salary.setStatus(SalaryStatus.DRAFT);
        return salaryRepository.save(salary);
    }

    @Override
    @Transactional
    public Salary update(Salary salary, Integer id) {
        Salary salaryFound = salaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find salary with id: " + id));
        if (!isDraftStatus(salaryFound.getStatus())) {
            throw new IllegalStateException("Only DRAFT salary can be updated");
        }

        Integer employeeId = salary.getEmployee() == null ? null : salary.getEmployee().getId();
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee is required");
        }
        if (salaryRepository.existsConflictForUpdate(employeeId, salary.getMonth(), salary.getYear(), id)) {
            throw new IllegalArgumentException("Salary for this employee and month/year already exists");
        }

        salary.setId(salaryFound.getId());
        salary.setStatus(salaryFound.getStatus());
        salary.setCreatedAt(salaryFound.getCreatedAt());
        return salaryRepository.save(salary);
    }

    @Override
    @Transactional
    public int generateMonthFromAttendance(Integer month, Integer year, boolean overwriteDraft) {
        validatePeriod(month, year);

        List<AttendanceRepository.AttendanceWorkdaySummary> summaries =
                attendanceRepository.summarizeWorkedDaysByEmployeeInMonth(month, year);
        if (summaries.isEmpty()) {
            throw new NotFoundExeption("No attendance records found for " + month + "/" + year);
        }

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        int changed = 0;

        for (AttendanceRepository.AttendanceWorkdaySummary summary : summaries) {
            Integer employeeId = summary.getEmployeeId();
            long workedDays = summary.getWorkedDays() == null ? 0 : summary.getWorkedDays();
            if (employeeId == null || workedDays <= 0) {
                continue;
            }

            List<Contract> contracts = contractRepository.findLatestContractByEmployeeId(employeeId, PageRequest.of(0, 1));
            if (contracts.isEmpty()) {
                continue;
            }

            BigDecimal contractBase = resolveContractBase(contracts.get(0));
            BigDecimal attendanceBase = contractBase
                    .multiply(BigDecimal.valueOf(workedDays))
                    .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
            BigDecimal rewardAmount = rewardRepository.sumRewardByEmployeeInMonth(employeeId, month, year);
            BigDecimal allowance = rewardAmount == null ? BigDecimal.ZERO : rewardAmount;
            BigDecimal deduction = BigDecimal.ZERO;
            BigDecimal total = attendanceBase.add(allowance).subtract(deduction);

            Optional<Salary> existingSalary = salaryRepository.findByEmployee_IdAndMonthAndYear(employeeId, month, year);
            if (existingSalary.isPresent()) {
                Salary salary = existingSalary.get();
                if (salary.getStatus() == SalaryStatus.FINALIZED || salary.getStatus() == SalaryStatus.PAID) {
                    continue;
                }
                if (!overwriteDraft && isDraftStatus(salary.getStatus())) {
                    continue;
                }
                salary.setBaseSalary(attendanceBase);
                salary.setAllowance(allowance);
                salary.setDeduction(deduction);
                salary.setTotalSalary(total);
                salary.setStatus(SalaryStatus.DRAFT);
                salaryRepository.save(salary);
                changed++;
                continue;
            }

            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            if (employee == null) {
                continue;
            }

            Salary salary = new Salary();
            salary.setEmployee(employee);
            salary.setMonth(month);
            salary.setYear(year);
            salary.setBaseSalary(attendanceBase);
            salary.setAllowance(allowance);
            salary.setDeduction(deduction);
            salary.setTotalSalary(total);
            salary.setStatus(SalaryStatus.DRAFT);
            salary.setCreatedAt(Instant.now());
            salaryRepository.save(salary);
            changed++;
        }

        return changed;
    }

    @Override
    @Transactional
    public int finalizeMonth(Integer month, Integer year) {
        validatePeriod(month, year);
        long recordsInPeriod = salaryRepository.countByMonthAndYear(month, year);
        if (recordsInPeriod == 0) {
            throw new NotFoundExeption("No salary records found for " + month + "/" + year);
        }
        return salaryRepository.bulkUpdateStatusByPeriod(month, year, SalaryStatus.DRAFT, SalaryStatus.FINALIZED);
    }

    @Override
    @Transactional
    public int payMonth(Integer month, Integer year) {
        validatePeriod(month, year);
        long recordsInPeriod = salaryRepository.countByMonthAndYear(month, year);
        if (recordsInPeriod == 0) {
            throw new NotFoundExeption("No salary records found for " + month + "/" + year);
        }
        return salaryRepository.bulkUpdateStatusByPeriodFromMany(
                month,
                year,
                List.of(SalaryStatus.DRAFT, SalaryStatus.FINALIZED),
                SalaryStatus.PAID
        );
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Salary salaryFound = salaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find salary with id: " + id));
        if (!isDraftStatus(salaryFound.getStatus())) {
            throw new IllegalStateException("Only DRAFT salary can be deleted");
        }
        salaryRepository.deleteById(id);
    }

    private boolean isDraftStatus(SalaryStatus status) {
        return status == null || status == SalaryStatus.DRAFT;
    }

    private void validatePeriod(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 2000 || year > 3000) {
            throw new IllegalArgumentException("Year must be between 2000 and 3000");
        }
    }

    private BigDecimal resolveContractBase(Contract contract) {
        BigDecimal baseSalary = contract.getBaseSalary() == null ? BigDecimal.ZERO : contract.getBaseSalary();
        BigDecimal coefficient = contract.getSalaryCoefficient() == null ? BigDecimal.ONE : contract.getSalaryCoefficient();
        return baseSalary.multiply(coefficient).setScale(2, RoundingMode.HALF_UP);
    }
}
