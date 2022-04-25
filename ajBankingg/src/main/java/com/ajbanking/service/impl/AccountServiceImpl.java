package com.ajbanking.service.impl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

import com.ajbanking.dao.SavingsTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajbanking.dao.PrimaryAccountDao;
import com.ajbanking.dao.SavingsAccountDao;
import com.ajbanking.domain.PrimaryAccount;
import com.ajbanking.domain.PrimaryTransaction;
import com.ajbanking.domain.SavingsAccount;
import com.ajbanking.domain.SavingsTransaction;
import com.ajbanking.domain.User;
import com.ajbanking.service.AccountService;
import com.ajbanking.service.TransactionService;
import com.ajbanking.service.UserService;

@Service
public class AccountServiceImpl implements AccountService {

    private static int nextAccountNumber = (int) (Math.random()*Math.pow(8,8));

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SavingsTransactionDao savingsTransactionDao;

    @Autowired
    private TransactionService transactionService;
    
    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0.0));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);
        
        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
   }
    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0.0));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }
    
    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);
        }
    }

    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
        }
    }

    public void depositToSomeoneElseViaSavings(String username , BigDecimal amt){
        User user = userService.findByUsername(username);
        SavingsAccount savings = user.getSavingsAccount();
        savings.setAccountBalance(savings.getAccountBalance().add(amt));
        Double amount = amt.doubleValue();
        savingsAccountDao.save(savings);
        Date date = new Date();
        SavingsTransaction savingsTransaction = new SavingsTransaction(date ,"from someOne" , "Account" , "finished" , amount , savings.getAccountBalance() , savings);
        transactionService.saveSavingsDepositTransaction(savingsTransaction);
//        SavingsAccount acc = savingsAccountDao.findAccountNumber(recipientAccount);

    }

//    public void depositToSomeoneElseViaPrimary(String username , BigDecimal amt){
//        User user = userService.findByUsername(username);
//        PrimaryAccount primary = user.getPrimaryAccount();
//        primary.setAccountBalance(primary.getAccountBalance().add(amt));
//        primaryAccountDao.save(primary);
////        SavingsAccount acc = savingsAccountDao.findAccountNumber(recipientAccount);
//
//    }


    private int accountGen() {
    	return ++nextAccountNumber;
    }
}




