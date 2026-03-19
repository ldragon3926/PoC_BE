package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Permissions;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.PermissionRepository;
import org.example.poc.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public List<Permissions> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Optional<Permissions> findById(Integer id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Permissions getOne(Integer id) {
        return permissionRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Permissions add(Permissions permissions) {
        return permissionRepository.save(permissions);
    }

    @Override
    @Transactional
    public Permissions update(Permissions permissions, Integer id) {
        Permissions permissionFound = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find permission with id: " + id));
        permissions.setId(permissionFound.getId());
        return permissionRepository.save(permissions);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        permissionRepository.deleteById(id);
    }
}
