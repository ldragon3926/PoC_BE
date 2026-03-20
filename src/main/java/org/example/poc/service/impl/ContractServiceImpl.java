package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Contract;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.ContractRepository;
import org.example.poc.service.ContractService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAllWithEmployee();
    }

    @Override
    public Optional<Contract> findById(Integer id) {
        return contractRepository.findByIdWithEmployee(id);
    }

    @Override
    public Contract getOne(Integer id) {
        return contractRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find contract with id: " + id));
    }

    @Override
    @Transactional
    public Contract add(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Contract update(Contract contract, Integer id) {
        Contract contractFound = contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find contract with id: " + id));
        contract.setId(contractFound.getId());
        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        contractRepository.deleteById(id);
    }
}
