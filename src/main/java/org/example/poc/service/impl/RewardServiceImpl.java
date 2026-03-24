package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Reward;
import org.example.poc.entity.Salary;
import org.example.poc.entity.SalaryStatus;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.RewardRepository;
import org.example.poc.repository.SalaryRepository;
import org.example.poc.service.RewardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;
    private final SalaryRepository salaryRepository;

    @Override
    public List<Reward> findAll() {
        return rewardRepository.findAllWithEmployee();
    }

    @Override
    public Optional<Reward> findById(Integer id) {
        return rewardRepository.findByIdWithEmployee(id);
    }

    @Override
    public Reward getOne(Integer id) {
        return rewardRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find reward with id: " + id));
    }

    @Override
    @Transactional
    public Reward add(Reward reward) {
        if (reward.getCreatedAt() == null) {
            reward.setCreatedAt(Instant.now());
        }
        Reward saved = rewardRepository.save(reward);
        syncSalaryAllowance(saved.getEmployee() == null ? null : saved.getEmployee().getId());
        return saved;
    }

    @Override
    @Transactional
    public Reward update(Reward reward, Integer id) {
        Reward rewardFound = rewardRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find reward with id: " + id));
        Integer oldEmployeeId = rewardFound.getEmployee() == null ? null : rewardFound.getEmployee().getId();

        reward.setId(rewardFound.getId());
        if (reward.getCreatedAt() == null) {
            reward.setCreatedAt(rewardFound.getCreatedAt() != null ? rewardFound.getCreatedAt() : Instant.now());
        }

        Reward saved = rewardRepository.save(reward);
        syncSalaryAllowance(oldEmployeeId);
        syncSalaryAllowance(saved.getEmployee() == null ? null : saved.getEmployee().getId());
        return saved;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Reward rewardFound = rewardRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find reward with id: " + id));
        Integer employeeId = rewardFound.getEmployee() == null ? null : rewardFound.getEmployee().getId();
        rewardRepository.deleteById(id);
        syncSalaryAllowance(employeeId);
    }

    private void syncSalaryAllowance(Integer employeeId) {
        if (employeeId == null) {
            return;
        }
        List<Salary> mutableSalaries = salaryRepository.findMutableByEmployeeId(
                employeeId,
                Arrays.asList(SalaryStatus.FINALIZED, SalaryStatus.PAID)
        );
        for (Salary salary : mutableSalaries) {
            Integer month = salary.getMonth();
            Integer year = salary.getYear();
            if (month == null || year == null) {
                continue;
            }
            BigDecimal allowance = rewardRepository.sumRewardByEmployeeInMonth(employeeId, month, year);
            if (allowance == null) {
                allowance = BigDecimal.ZERO;
            }
            BigDecimal baseSalary = salary.getBaseSalary() == null ? BigDecimal.ZERO : salary.getBaseSalary();
            BigDecimal deduction = salary.getDeduction() == null ? BigDecimal.ZERO : salary.getDeduction();
            BigDecimal total = baseSalary.add(allowance).subtract(deduction);

            salary.setAllowance(allowance);
            salary.setTotalSalary(total);
            if (salary.getStatus() == null) {
                salary.setStatus(SalaryStatus.DRAFT);
            }
            salaryRepository.save(salary);
        }
    }
}
