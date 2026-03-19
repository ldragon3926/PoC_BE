package org.example.poc.service;

import org.example.poc.entity.TokenBlackList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TokenBlackListService {
    List<TokenBlackList> findAll();
    TokenBlackList add(TokenBlackList tokenBlackList);
    TokenBlackList update(TokenBlackList tokenBlackList, Integer id);
    void delete(Integer id);
}
