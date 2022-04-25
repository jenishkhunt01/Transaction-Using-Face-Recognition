package com.ajbanking.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.ajbanking.domain.Recipient;

public interface RecipientDao extends CrudRepository<Recipient, Long> {
	
    List<Recipient> findAll();

    Recipient findByName(String recipientName);

    void deleteByName(String reciepentName);

    Recipient findByAccountNumber(String accountNumber);
}
