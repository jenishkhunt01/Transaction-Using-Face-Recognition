package com.ajbanking.service;

import java.security.Principal;
import java.util.List;

import com.ajbanking.domain.PrimaryAccount;
import com.ajbanking.domain.PrimaryTransaction;
import com.ajbanking.domain.Recipient;
import com.ajbanking.domain.SavingsAccount;
import com.ajbanking.domain.SavingsTransaction;

public interface TransactionService {

        List<PrimaryTransaction> findPrimaryTransactionList(String username);
    
        List<SavingsTransaction> findSavingsTransactionList(String username);
    
        void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction);
    
        void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction);
        
    	void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction);
    	
    	void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction);
    	
    	void betweenAccountsTransfer(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception;

        List<Recipient> findRecipientList(Principal principal);

        Recipient saveRecipient(Recipient recipient);

        Recipient findRecipientByName(String recipientName);

        void deleteRecipientByName(String recipientName);
        
        void toSomeoneElseTransfer(Recipient recipient, String accountType, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount);

        Recipient findByAccountNumber(String accountNumber);
}
