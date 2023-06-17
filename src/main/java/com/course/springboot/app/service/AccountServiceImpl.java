package com.course.springboot.app.service;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.Bank;
import com.course.springboot.app.repositories.AccountRepository;
import com.course.springboot.app.repositories.BankRepository;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public int totalTransfersDone(Long bankId) {
        Bank bank = bankRepository.findById(bankId);
        return bank.getTotalTransfer();
    }

    @Override
    public BigDecimal checkBalance(Long id) {
        Account account = accountRepository.findById(id);
        return account.getBalance();
    }

    @Override
    public void transfer(Long numAccountOrigin, Long numAccountDestiny, BigDecimal amount,
                         Long bankId) {
        Bank bank = bankRepository.findById(bankId);
        int totalTrasfers = bank.getTotalTransfer();
        bank.setTotalTransfer(++totalTrasfers);
        bankRepository.update(bank);

        Account accountOrigin = accountRepository.findById(numAccountOrigin);
        accountOrigin.debit(amount);
        accountRepository.update(accountOrigin);

        Account accountDestiny = accountRepository.findById(numAccountDestiny);
        accountDestiny.credit(amount);
        accountRepository.update(accountDestiny);
    }
}
