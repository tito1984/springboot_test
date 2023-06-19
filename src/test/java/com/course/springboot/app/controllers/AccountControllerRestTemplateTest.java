package com.course.springboot.app.controllers;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransfer() throws JsonProcessingException {
        TransactionDTO dto = new TransactionDTO(1L,2L,new BigDecimal("100"),1L);

        ResponseEntity<String> response = client.
                postForEntity("/api/accounts/transfer", dto, String.class);

        String json = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transfer done"));
        assertTrue(json.contains("{\"accountOriginId\":1,\"accountDestinyId\":2,\"amount\":100,\"bankId\":1}"));

        JsonNode jsonNode = objectMapper.readTree(json);

        assertEquals("Transfer done", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaction").path("amount").asText());
        assertEquals(1L, jsonNode.path("transaction").path("accountOriginId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("message", "Transfer done");
        response2.put("transaction", dto);

        assertEquals(objectMapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(2)
    void testFindById() {
        ResponseEntity<Account> response = client.getForEntity("/api/accounts/1", Account.class);
        Account account = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("Txema", account.getPerson());
        assertEquals("900.00", account.getBalance().toPlainString());
        assertEquals(new Account(1L, "Txema", new BigDecimal("900.00")), account);
    }

    @Test
    @Order(3)
    void testFindAll() throws JsonProcessingException {
        ResponseEntity<Account[]> response = client.getForEntity("/api/accounts", Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(2,accounts.size());
        assertEquals(1L, accounts.get(0).getId());
        assertEquals("Txema", accounts.get(0).getPerson());
        assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
        assertEquals(2L, accounts.get(1).getId());
        assertEquals("Pedro", accounts.get(1).getPerson());
        assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Txema", json.get(0).path("person").asText());
        assertEquals("900.0", json.get(0).path("balance").asText());
        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("Pedro", json.get(1).path("person").asText());
        assertEquals("2100.0", json.get(1).path("balance").asText());

    }

    @Test
    @Order(4)
    void testSave() {
        Account account = new Account(null, "Pepa", new BigDecimal("3000"));

        ResponseEntity<Account> response = client.postForEntity("/api/accounts", account, Account.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        Account createdAccount = response.getBody();
        assertNotNull(createdAccount);
        assertEquals(3L, createdAccount.getId());
        assertEquals("Pepa", createdAccount.getPerson());
        assertEquals("3000", createdAccount.getBalance().toPlainString());
    }

    @Test
    @Order(5)
    void testDelete() {
        ResponseEntity<Account[]> response = client.getForEntity("/api/accounts", Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());
        assertEquals(3,accounts.size());

//        client.delete("/api/accounts/3");
        Map<String, Long> pathVariable = new HashMap<>();
        pathVariable.put("id", 3L);
        ResponseEntity<Void> exchange = client.exchange("/api/accounts/3", HttpMethod.DELETE, null
                , Void.class, pathVariable);
        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        response = client.getForEntity("/api/accounts", Account[].class);
        accounts = Arrays.asList(response.getBody());
        assertEquals(2,accounts.size());

        ResponseEntity<Account> responseDetail = client.getForEntity("/api/accounts/3", Account.class);
        assertEquals(HttpStatus.NOT_FOUND, responseDetail.getStatusCode());
        assertFalse(responseDetail.hasBody());
    }
}