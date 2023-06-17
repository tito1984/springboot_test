package com.course.springboot.app.repositories;

import com.course.springboot.app.models.Bank;

import java.util.List;

public interface BankRepository {
    List<Bank> findAll();
    Bank findById(Long id);
    void update(Bank bank);
}
