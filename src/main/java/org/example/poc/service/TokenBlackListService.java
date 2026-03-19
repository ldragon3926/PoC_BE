package org.example.poc.service;

import org.example.poc.entity.TokenBlackList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TokenBlackListService {
    List<TokenBlackList> findAll();
    Optional<TokenBlackList> findById(Integer id);
    TokenBlackList getOne(Integer id);
    TokenBlackList add(TokenBlackList tokenBlackList);
    TokenBlackList update(TokenBlackList tokenBlackList, Integer id);
    void delete(Integer id);
}
