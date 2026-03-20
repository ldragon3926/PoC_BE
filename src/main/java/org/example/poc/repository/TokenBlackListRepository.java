package org.example.poc.repository;

import org.example.poc.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Integer> {
    boolean existsByToken(String token);
}
