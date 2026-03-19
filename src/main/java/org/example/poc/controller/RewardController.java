package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Reward.RewardSet;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Reward;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reward")
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(rewardService.findAll(), "Get reward list successfully", "VIEW_REWARD_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Reward> reward = rewardService.findById(id);
        if (reward.isEmpty()) {
            return ResponseUltils.error("error.reward.not_found", "Reward does not exist");
        }
        return ResponseUltils.success(rewardService.getOne(id), "Get reward detail successfully", "VIEW_REWARD_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid RewardSet rewardSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.reward.validation", error);
        }
        Employee employee = employeeRepository.findById(rewardSet.getEmployeeId()).orElse(null);
        Reward reward = rewardSet.dto(new Reward(), employee);
        return ResponseUltils.success(rewardService.add(reward), "Create reward successfully", "VIEW_REWARD_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid RewardSet rewardSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.reward.validation", error);
        }
        Employee employee = employeeRepository.findById(rewardSet.getEmployeeId()).orElse(null);
        Reward reward = rewardSet.dto(new Reward(), employee);
        return ResponseUltils.success(rewardService.update(reward, id), "Update reward successfully", "VIEW_REWARD_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (rewardService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.reward.not_found", "Reward does not exist");
        }
        rewardService.delete(id);
        return ResponseUltils.success(null, "Delete reward successfully", "VIEW_REWARD_DELETE");
    }
}
