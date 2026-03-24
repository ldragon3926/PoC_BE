package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Salary.SalarySet;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Salary;
import org.example.poc.entity.SalaryStatus;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer employeeId,
                                     @RequestParam(required = false) Integer month,
                                     @RequestParam(required = false) Integer year,
                                     @RequestParam(required = false) SalaryStatus status) {
        return ResponseUltils.success(
                salaryService.findAllFiltered(keyword, employeeId, month, year, status),
                "Get salary list successfully",
                "VIEW_SALARY_LIST"
        );
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Salary> salary = salaryService.findById(id);
        if (salary.isEmpty()) {
            return ResponseUltils.error("error.salary.not_found", "Salary does not exist");
        }
        return ResponseUltils.success(salaryService.getOne(id), "Get salary detail successfully", "VIEW_SALARY_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid SalarySet salarySet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.salary.validation", error);
        }
        Employee employee = employeeRepository.findById(salarySet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.salary.employee_not_found", "Employee does not exist");
        }
        try {
            Salary salary = salarySet.dto(new Salary(), employee);
            return ResponseUltils.success(salaryService.add(salary), "Create salary successfully", "VIEW_SALARY_CREATE");
        } catch (IllegalArgumentException ex) {
            return ResponseUltils.error("error.salary.duplicate_period", ex.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid SalarySet salarySet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.salary.validation", error);
        }
        Employee employee = employeeRepository.findById(salarySet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.salary.employee_not_found", "Employee does not exist");
        }
        try {
            Salary salary = salarySet.dto(new Salary(), employee);
            return ResponseUltils.success(salaryService.update(salary, id), "Update salary successfully", "VIEW_SALARY_UPDATE");
        } catch (IllegalStateException ex) {
            return ResponseUltils.error("error.salary.finalized_locked", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseUltils.error("error.salary.duplicate_period", ex.getMessage());
        }
    }

    @PutMapping("/update/finalize")
    public ResponseEntity<?> finalizeMonth(@RequestParam Integer month, @RequestParam Integer year) {
        if (month == null || month < 1 || month > 12) {
            return ResponseUltils.error("error.salary.validation", "Month must be between 1 and 12");
        }
        if (year == null || year < 2000 || year > 3000) {
            return ResponseUltils.error("error.salary.validation", "Year must be between 2000 and 3000");
        }
        try {
            int finalizedRows = salaryService.finalizeMonth(month, year);
            String message = "Finalize salary period " + month + "/" + year + " successfully. Updated " + finalizedRows + " record(s).";
            return ResponseUltils.success(finalizedRows, message, "VIEW_SALARY_UPDATE");
        } catch (IllegalArgumentException ex) {
            return ResponseUltils.error("error.salary.validation", ex.getMessage());
        } catch (NotFoundExeption ex) {
            return ResponseUltils.error("error.salary.period_not_found", ex.getMessage());
        }
    }

    @PutMapping("/update/pay")
    public ResponseEntity<?> payMonth(@RequestParam Integer month, @RequestParam Integer year) {
        if (month == null || month < 1 || month > 12) {
            return ResponseUltils.error("error.salary.validation", "Month must be between 1 and 12");
        }
        if (year == null || year < 2000 || year > 3000) {
            return ResponseUltils.error("error.salary.validation", "Year must be between 2000 and 3000");
        }
        try {
            int paidRows = salaryService.payMonth(month, year);
            String message = "Mark salary period " + month + "/" + year + " as PAID successfully. Updated " + paidRows + " record(s).";
            return ResponseUltils.success(paidRows, message, "VIEW_SALARY_UPDATE");
        } catch (IllegalArgumentException ex) {
            return ResponseUltils.error("error.salary.validation", ex.getMessage());
        } catch (NotFoundExeption ex) {
            return ResponseUltils.error("error.salary.period_not_found", ex.getMessage());
        }
    }

    @PostMapping("/create/generate")
    public ResponseEntity<?> generateMonth(@RequestParam Integer month,
                                           @RequestParam Integer year,
                                           @RequestParam(defaultValue = "true") Boolean overwriteDraft) {
        try {
            int generatedRows = salaryService.generateMonthFromAttendance(month, year, Boolean.TRUE.equals(overwriteDraft));
            String message = "Generate salary period " + month + "/" + year + " successfully. Changed " + generatedRows + " record(s).";
            return ResponseUltils.success(generatedRows, message, "VIEW_SALARY_CREATE");
        } catch (IllegalArgumentException ex) {
            return ResponseUltils.error("error.salary.validation", ex.getMessage());
        } catch (NotFoundExeption ex) {
            return ResponseUltils.error("error.salary.period_not_found", ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (salaryService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.salary.not_found", "Salary does not exist");
        }
        try {
            salaryService.delete(id);
            return ResponseUltils.success(null, "Delete salary successfully", "VIEW_SALARY_DELETE");
        } catch (IllegalStateException ex) {
            return ResponseUltils.error("error.salary.finalized_locked", ex.getMessage());
        }
    }
}
