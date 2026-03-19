package org.example.poc.service;

import org.example.poc.entity.Reward;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RewardService {
    List<Reward> findAll();
    Optional<Reward> findById(Integer id);
    Reward getOne(Integer id);
    Reward add(Reward reward);
    Reward update(Reward reward, Integer id);
    void delete(Integer id);
}
