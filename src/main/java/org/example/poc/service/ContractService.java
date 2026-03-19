package org.example.poc.service;

import org.example.poc.entity.Contract;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContractService {
    List<Contract> findAll();
    Contract add(Contract contract);
    Contract update(Contract contract, Integer id);
    void delete(Integer id);
}
