package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.TokenBlackList;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.TokenBlackListRepository;
import org.example.poc.service.TokenBlackListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenBlackListServiceImpl implements TokenBlackListService {
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    public List<TokenBlackList> findAll() {
        return tokenBlackListRepository.findAll();
    }

    @Override
    @Transactional
    public TokenBlackList add(TokenBlackList tokenBlackList) {
        return tokenBlackListRepository.save(tokenBlackList);
    }

    @Override
    @Transactional
    public TokenBlackList update(TokenBlackList tokenBlackList, Integer id) {
        TokenBlackList tokenBlackListFound = tokenBlackListRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find token blacklist with id: " + id));
        tokenBlackList.setId(tokenBlackListFound.getId());
        return tokenBlackListRepository.save(tokenBlackList);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        tokenBlackListRepository.deleteById(id);
    }
}
