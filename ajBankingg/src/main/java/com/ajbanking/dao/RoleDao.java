package com.ajbanking.dao;

import org.springframework.data.repository.CrudRepository;

import com.ajbanking.domain.security.Role;

public interface RoleDao extends CrudRepository<Role, Integer> {
    Role findByName(String name);
}

