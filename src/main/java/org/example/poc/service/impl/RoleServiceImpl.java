package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.RoleRepository;
import org.example.poc.repository.UserRepository;
import org.example.poc.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;
    @Override
    public List<Roles>  findAll(){
       return roleRepository.findAll();
    }


    @Override
    @Transactional
    public Roles add(Roles roles){
    return roleRepository.save(roles);
    }

    @Override
    @Transactional
    public Roles update(Roles roles, Integer id){
        Roles rFind = roleRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Không tìm thấy người dùng với id: "+id));
        roles.setId(rFind.getId());
        return roleRepository.save(roles);
    }

    @Override
    @Transactional
    public void delete(Integer id){
        roleRepository.deleteById(id);
    }
}
