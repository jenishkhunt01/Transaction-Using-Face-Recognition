package com.ajbanking.service;

import java.math.BigDecimal;
import java.security.Principal;

import com.ajbanking.domain.PrimaryAccount;
import com.ajbanking.domain.SavingsAccount;

public interface AccountService {
	PrimaryAccount createPrimaryAccount();
	SavingsAccount createSavingsAccount();
	void deposit(String accountType, double amount, Principal principal);
	void withdraw(String accountType, double amount, Principal principal);
	public void depositToSomeoneElseViaSavings(String  username, BigDecimal amt);
//	public void depositToSomeoneElseViaPrimary(String  username, BigDecimal amt);
}
