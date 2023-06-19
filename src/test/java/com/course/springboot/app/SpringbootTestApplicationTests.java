package com.course.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.course.springboot.app.Data.*;

import com.course.springboot.app.exceptions.NotEnoughMoneyException;
import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.Bank;
import com.course.springboot.app.repositories.AccountRepository;
import com.course.springboot.app.repositories.BankRepository;
import com.course.springboot.app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	AccountRepository accountRepository;
	@MockBean
	BankRepository bankRepository;

	@Autowired
	AccountService service;

	@BeforeEach
	void setUp() {
//		accountRepository = mock(AccountRepository.class);
//		bankRepository = mock(BankRepository.class);
//		service = new AccountServiceImpl(accountRepository, bankRepository);
//		Data.ACC_001.setBalance(new BigDecimal("1000"));
//		Data.ACC_002.setBalance(new BigDecimal("2000"));
//		Data.BANK.setTotalTransfer(0);
	}

	@Test
	void contextLoads() {
		when(accountRepository.findById(1L)).thenReturn(create_acc001());
		when(accountRepository.findById(2L)).thenReturn(create_acc002());
		when(bankRepository.findById(1L)).thenReturn(create_bank());

		BigDecimal originBalance = service.checkBalance(1L);
		BigDecimal destinyBalance = service.checkBalance(2L);

		assertEquals("1000", originBalance.toPlainString());
		assertEquals("2000", destinyBalance.toPlainString());

		service.transfer(1L, 2L, new BigDecimal("100"), 1L);

		originBalance = service.checkBalance(1L);
		destinyBalance = service.checkBalance(2L);

		assertEquals("900", originBalance.toPlainString());
		assertEquals("2100", destinyBalance.toPlainString());

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(3)).findById(2L);
		verify(accountRepository, times(2)).save(any(Account.class));

		int total = service.totalTransfersDone(1L);
		assertEquals(1, total);

		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository).save(any(Bank.class));

		verify(accountRepository, never()).findAll();
		verify(accountRepository, times(6)).findById(anyLong());
	}

	@Test
	void contextLoadsExceptions() {
		when(accountRepository.findById(1L)).thenReturn(create_acc001());
		when(accountRepository.findById(2L)).thenReturn(create_acc002());
		when(bankRepository.findById(1L)).thenReturn(create_bank());

		BigDecimal originBalance = service.checkBalance(1L);
		BigDecimal destinyBalance = service.checkBalance(2L);

		assertEquals("1000", originBalance.toPlainString());
		assertEquals("2000", destinyBalance.toPlainString());

		assertThrows(NotEnoughMoneyException.class, () -> {
			service.transfer(1L, 2L, new BigDecimal("1200"), 1L);
		});


		originBalance = service.checkBalance(1L);
		destinyBalance = service.checkBalance(2L);

		assertEquals("1000", originBalance.toPlainString());
		assertEquals("2000", destinyBalance.toPlainString());

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(2)).findById(2L);
		verify(accountRepository, never()).save(any(Account.class));

		int total = service.totalTransfersDone(1L);
		assertEquals(0, total);

		verify(bankRepository, times(1)).findById(1L);
		verify(bankRepository, never()).save(any(Bank.class));
		verify(accountRepository, never()).findAll();
		verify(accountRepository, times(5)).findById(anyLong());

	}

	@Test
	void contextLoadTest() {
		when(accountRepository.findById(1L)).thenReturn(create_acc001());

		Account account1 = service.findById(1L);
		Account account2 = service.findById(1L);

		assertSame(account1, account2);
		assertTrue(account1 == account2);
		assertEquals("Txema", account1.getPerson());
		assertEquals("Txema", account1.getPerson());
		verify(accountRepository, times(2)).findById(1L);
	}
}
