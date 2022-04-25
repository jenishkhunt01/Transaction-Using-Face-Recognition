package com.ajbanking.dao;

import com.ajbanking.domain.SavingsAccount;
import org.springframework.data.repository.CrudRepository;

public interface SavingsAccountDao extends CrudRepository<SavingsAccount, Long> {

    SavingsAccount findByAccountNumber (int accountNumber);

//    SavingsAccount findAccountNumber(St accountNumber)
}
