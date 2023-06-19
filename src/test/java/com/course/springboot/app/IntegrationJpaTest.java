package com.course.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.course.springboot.app.Data.*;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
public class IntegrationJpaTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void testFindById() {
        Optional<Account> account = accountRepository.findById(1L);

        assertTrue(account.isPresent());
        assertEquals("Txema", account.orElseThrow().getPerson());
    }

    @Test
    void testFindByPerson() {
        Optional<Account> account = accountRepository.findByPerson("Txema");

        assertTrue(account.isPresent());
        assertEquals("Txema", account.orElseThrow().getPerson());
        assertEquals("1000.00", account.orElseThrow().getBalance().toPlainString());
    }

    @Test
    void testFindByPersonThrowException() {
        Optional<Account> account = accountRepository.findByPerson("Ron");

        assertThrows(NoSuchElementException.class, account::orElseThrow);
        assertFalse(account.isPresent());
    }

    @Test
    void testFindAll() {
        List<Account> accounts = accountRepository.findAll();
        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    void testSave() {
        Account accountPepe = new Account(null, "Pepe", new BigDecimal("3000"));

        Account account = accountRepository.save(accountPepe);
//        Account account = accountRepository.findByPerson(accountPepe.getPerson()).orElseThrow();
//        Account account =  accountRepository.findById(save.getId()).orElseThrow();

        assertEquals("3000", account.getBalance().toPlainString());
        assertEquals("Pepe", account.getPerson());
//        assertEquals(3, account.getId());
    }

    @Test
    void testUpdate() {
        Account accountPepe = new Account(null, "Pepe", new BigDecimal("3000"));

        Account account = accountRepository.save(accountPepe);
//        Account account = accountRepository.findByPerson(accountPepe.getPerson()).orElseThrow();
//        Account account =  accountRepository.findById(save.getId()).orElseThrow();

        assertEquals("3000", account.getBalance().toPlainString());
        assertEquals("Pepe", account.getPerson());
//        assertEquals(3, account.getId());

        account.setBalance(new BigDecimal("3800"));
        Account accountUpdated = accountRepository.save(account);

        assertEquals("3800", accountUpdated.getBalance().toPlainString());
        assertEquals("Pepe", accountUpdated.getPerson());
    }

    @Test
    void testDelete() {
        Account account = accountRepository.findById(2L).orElseThrow();

        assertEquals("Pedro", account.getPerson());

        accountRepository.delete(account);

        assertThrows(NoSuchElementException.class, () -> {
            accountRepository.findByPerson("Pedro").orElseThrow();
//            accountRepository.findById(2L).orElseThrow();
        });
    }
}
