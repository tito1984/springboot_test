package com.course.springboot.app.service;

import com.course.springboot.app.models.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {
    Account findById(Long id);
    int totalTransfersDone(Long bankId);
    BigDecimal checkBalance(Long id);
    void transfer(Long numAccountOrigin, Long numAccountDestiny, BigDecimal amount,
                  Long bankId);
}
