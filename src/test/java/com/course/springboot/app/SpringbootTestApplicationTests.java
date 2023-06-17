package com.course.springboot.app;

import com.course.springboot.app.repositories.AccountRepository;
import com.course.springboot.app.repositories.BankRepository;
import com.course.springboot.app.service.AccountService;
import com.course.springboot.app.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootTestApplicationTests {

	AccountRepository accountRepository;
	BankRepository bankRepository;

	AccountService service;

	@BeforeEach
	void setUp() {
		accountRepository = mock(AccountRepository.class);
		bankRepository = mock(BankRepository.class);
		service = new AccountServiceImpl(accountRepository, bankRepository);
	}

	@Test
	void contextLoads() {

	}

}
