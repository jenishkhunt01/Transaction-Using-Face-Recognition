package com.ajbanking.dao;

import org.springframework.data.repository.CrudRepository;
import com.ajbanking.domain.User;

public interface UserDao extends CrudRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);
}
