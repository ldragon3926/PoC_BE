package org.example.poc.service;

import org.example.poc.entity.Contract;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ContractService {
    List<Contract> findAll();
    Optional<Contract> findById(Integer id);
    Contract getOne(Integer id);
    Contract add(Contract contract);
    Contract update(Contract contract, Integer id);
    void delete(Integer id);
}
