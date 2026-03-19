package org.example.poc.controller;

import lombok.RequiredArgsConstructor;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reward")
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(rewardService.findAll(), "Get reward list successfully", "VIEW_REWARD_LIST");
    }
}
