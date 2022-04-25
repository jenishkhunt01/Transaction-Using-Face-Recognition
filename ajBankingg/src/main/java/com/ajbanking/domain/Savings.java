package com.ajbanking.domain;

import java.math.BigDecimal;
import java.util.List;

public class Savings {
	private long id;
	private int accountNumber;
	private BigDecimal accountBalance;
	
	private List<SavingsTransaction> savingsTransactionList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public List<SavingsTransaction> getSavingsTransactionList() {
		return savingsTransactionList;
	}

	public void setSavingsTransactionList(List<SavingsTransaction> savingsTransactionList) {
		this.savingsTransactionList = savingsTransactionList;
	}
}
