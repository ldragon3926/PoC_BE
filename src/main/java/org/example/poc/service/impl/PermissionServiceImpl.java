package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import org.example.poc.entity.Permissions;
import org.example.poc.entity.Roles;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.PermissionRepository;
import org.example.poc.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    PermissionRepository permissionRepository;
    @Override
    public List<Permissions>  findAll(){
       return permissionRepository.findAll();
    }


    @Override
    @Transactional
    public Permissions add(Permissions permissions){
    return permissionRepository.save(permissions);
    }

    @Override
    @Transactional
    public Permissions update(Permissions permissions, Integer id){
        Permissions pFind = permissionRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Can not find permission with id: "+id));
        permissions.setId(pFind.getId());
        return permissionRepository.save(permissions);
    }

    @Override
    @Transactional
    public void delete(Integer id){
        permissionRepository.deleteById(id);
    }
}
