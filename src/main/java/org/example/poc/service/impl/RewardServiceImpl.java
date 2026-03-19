package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Reward;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.RewardRepository;
import org.example.poc.service.RewardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;

    @Override
    public List<Reward> findAll() {
        return rewardRepository.findAll();
    }

    @Override
    @Transactional
    public Reward add(Reward reward) {
        return rewardRepository.save(reward);
    }

    @Override
    @Transactional
    public Reward update(Reward reward, Integer id) {
        Reward rewardFound = rewardRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find reward with id: " + id));
        reward.setId(rewardFound.getId());
        return rewardRepository.save(reward);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        rewardRepository.deleteById(id);
    }
}
