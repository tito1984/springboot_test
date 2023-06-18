package com.course.springboot.app.repositories;

import com.course.springboot.app.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
//    List<Bank> findAll();
//    Optional<Bank> findById(Long id);
//    void update(Bank bank);
}
