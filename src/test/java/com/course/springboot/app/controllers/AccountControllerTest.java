package com.course.springboot.app.controllers;

import static com.course.springboot.app.Data.*;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.TransactionDTO;
import com.course.springboot.app.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFindById() throws Exception {
        when(accountService.findById(1L)).thenReturn(create_acc001().orElseThrow());

        mockMvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.person").value("Txema"))
                .andExpect(jsonPath("$.balance").value("1000"));
        verify(accountService).findById(1L);
    }

    @Test
    void testTransfer() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(1L, 2L, new BigDecimal("100"), 1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer done");
        response.put("transaction", transactionDTO);

        mockMvc.perform(post("/api/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transfer done"))
                .andExpect(jsonPath("$.transaction.accountOriginId").value(transactionDTO.getAccountOriginId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(accountService).transfer(transactionDTO.getAccountOriginId(),transactionDTO.getAccountDestinyId(),
                transactionDTO.getAmount(),transactionDTO.getBankId());
    }

    @Test
    void testListAll() throws Exception {
        List<Account> accounts = Arrays.asList(create_acc001().orElseThrow(),
                create_acc002().orElseThrow());
        when(accountService.findAll()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].person").value("Txema"))
                .andExpect(jsonPath("$[1].person").value("Pepe"))
                .andExpect(jsonPath("$[0].balance").value("1000"))
                .andExpect(jsonPath("$[1].balance").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
        verify(accountService).findAll();
    }

    @Test
    void testSave() throws Exception {
        Account account = new Account(null, "Pedro", new BigDecimal("3000"));
        when(accountService.save(any())).then(invocation -> {
            Account a = invocation.getArgument(0);
            a.setId(3L);
            return a;
        });

        mockMvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.person", is("Pedro")))
                .andExpect(jsonPath("$.balance", is(3000)));
        verify(accountService).save(any());
    }
}