package com.course.springboot.app.service;

import com.course.springboot.app.models.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    int totalTransfersDone(Long bankId);
    BigDecimal checkBalance(Long id);
    void transfer(Long numAccountOrigin, Long numAccountDestiny, BigDecimal amount,
                  Long bankId);
}
