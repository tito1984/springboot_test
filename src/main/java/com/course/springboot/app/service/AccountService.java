package com.course.springboot.app.service;

import com.course.springboot.app.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    void deleteById(Long id);
    int totalTransfersDone(Long bankId);
    BigDecimal checkBalance(Long id);
    void transfer(Long numAccountOrigin, Long numAccountDestiny, BigDecimal amount,
                  Long bankId);
}
