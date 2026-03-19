package org.example.poc.service;

import org.example.poc.entity.Reward;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RewardService {
    List<Reward> findAll();
    Reward add(Reward reward);
    Reward update(Reward reward, Integer id);
    void delete(Integer id);
}
