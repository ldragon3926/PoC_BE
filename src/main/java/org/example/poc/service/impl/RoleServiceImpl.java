package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Roles;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.RoleRepository;
import org.example.poc.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Roles> findAll() {
        return roleRepository.findAllWithPermissions();
    }

    @Override
    public Optional<Roles> findById(Integer id) {
        return roleRepository.findByIdWithPermissions(id);
    }

    @Override
    public Roles getOne(Integer id) {
        return roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find role with id: " + id));
    }

    @Override
    @Transactional
    public Roles add(Roles roles) {
        return roleRepository.save(roles);
    }

    @Override
    @Transactional
    public Roles update(Roles roles, Integer id) {
        Roles roleFound = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find role with id: " + id));
        roles.setId(roleFound.getId());
        return roleRepository.save(roles);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        roleRepository.deleteById(id);
    }
}
