package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Contract.ContractSet;
import org.example.poc.entity.Contract;
import org.example.poc.entity.Employee;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(contractService.findAll(), "Get contract list successfully", "VIEW_CONTRACT_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Contract> contract = contractService.findById(id);
        if (contract.isEmpty()) {
            return ResponseUltils.error("error.contract.not_found", "Contract does not exist");
        }
        return ResponseUltils.success(contractService.getOne(id), "Get contract detail successfully", "VIEW_CONTRACT_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid ContractSet contractSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.contract.validation", error);
        }
        Employee employee = employeeRepository.findById(contractSet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.contract.employee_not_found", "Employee does not exist");
        }
        Contract contract = contractSet.dto(new Contract(), employee);
        return ResponseUltils.success(contractService.add(contract), "Create contract successfully", "VIEW_CONTRACT_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid ContractSet contractSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.contract.validation", error);
        }
        Employee employee = employeeRepository.findById(contractSet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.contract.employee_not_found", "Employee does not exist");
        }
        Contract contract = contractSet.dto(new Contract(), employee);
        return ResponseUltils.success(contractService.update(contract, id), "Update contract successfully", "VIEW_CONTRACT_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (contractService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.contract.not_found", "Contract does not exist");
        }
        contractService.delete(id);
        return ResponseUltils.success(null, "Delete contract successfully", "VIEW_CONTRACT_DELETE");
    }
}
