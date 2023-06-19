package com.course.springboot.app.service;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.Bank;
import com.course.springboot.app.repositories.AccountRepository;
import com.course.springboot.app.repositories.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public int totalTransfersDone(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();

        return bank.getTotalTransfer();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal checkBalance(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        return account.getBalance();
    }

    @Override
    @Transactional
    public void transfer(Long numAccountOrigin, Long numAccountDestiny, BigDecimal amount,
                         Long bankId) {
        Account accountOrigin = accountRepository.findById(numAccountOrigin).orElseThrow();
        accountOrigin.debit(amount);
        accountRepository.save(accountOrigin);

        Account accountDestiny = accountRepository.findById(numAccountDestiny).orElseThrow();
        accountDestiny.credit(amount);
        accountRepository.save(accountDestiny);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTrasfers = bank.getTotalTransfer();
        bank.setTotalTransfer(++totalTrasfers);
        bankRepository.save(bank);
    }
}
