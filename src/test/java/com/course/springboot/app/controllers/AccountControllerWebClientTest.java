package com.course.springboot.app.controllers;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AccountControllerWebClientTest {

    private static final String BASE_URI = "http://localhost:8080/api/accounts";

    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransfer() throws JsonProcessingException {

        TransactionDTO transactionDTO = new TransactionDTO(1L,2L,new BigDecimal("300"),1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer done");
        response.put("transaction", transactionDTO);

        webTestClient.post().uri("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDTO)
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(res -> {
                    try {
                        JsonNode json = objectMapper.readTree(res.getResponseBody());
                        assertEquals("Transfer done", json.path("message").asText());
                        assertEquals(1L, json.path("transaction").path("accountOriginId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("300", json.path("transaction").path("amount").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transfer done"))
                .jsonPath("$.message").isEqualTo("Transfer done")
                .jsonPath("$.message").value(value -> assertEquals("Transfer done", value))
                .jsonPath("$.transaction.accountOriginId").isEqualTo(transactionDTO.getAccountOriginId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void testFindById() throws JsonProcessingException {
        Account account = new Account(1L, "Txema", new BigDecimal("700"));
        webTestClient.get().uri("/api/accounts/1")
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.person").isEqualTo("Txema")
                .jsonPath("$.balance").isEqualTo(700)
                .json(objectMapper.writeValueAsString(account));
    }

    @Test
    @Order(3)
    void testFindById2() {
        webTestClient.get().uri("/api/accounts/2")
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account account = response.getResponseBody();
                    assertEquals("Pedro", account.getPerson());
                    assertEquals("2300.00", account.getBalance().toPlainString());
                });
    }

    @Test
    @Order(4)
    void testfindAll() {
        webTestClient.get().uri("/api/accounts")
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].person").isEqualTo("Txema")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].balance").isEqualTo(700)
                .jsonPath("$[1].person").isEqualTo("Pedro")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].balance").isEqualTo(2300)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testfindAll2() {
        webTestClient.get().uri("/api/accounts")
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith(response -> {
                    List<Account> accounts = response.getResponseBody();
                    assertEquals(2, accounts.size());
                    assertEquals("Txema", accounts.get(0).getPerson());
                    assertEquals(1L, accounts.get(0).getId());
                    assertEquals("700.00", accounts.get(0).getBalance().toPlainString());
                    assertEquals("Pedro", accounts.get(01).getPerson());
                    assertEquals(2L, accounts.get(1).getId());
                    assertEquals("2300.00", accounts.get(1).getBalance().toPlainString());
                });
    }

    @Test
    @Order(6)
    void testSave() {
        Account account = new Account(null, "Pep", new BigDecimal("3000"));
        webTestClient.post().uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()

                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.person").isEqualTo("Pep")
                .jsonPath("$.person").value(is("Pep"))
                .jsonPath("$.balance").isEqualTo(3000)
                .jsonPath("$.id").isEqualTo(3);
    }

    @Test
    @Order(7)
    void testSave2() {
        Account account = new Account(null, "Pepa", new BigDecimal("3500"));
        webTestClient.post().uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()

                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account a = response.getResponseBody();
                    assertNotNull(a);
                    assertEquals("Pepa", a.getPerson());
                    assertEquals(4L, a.getId());
                    assertEquals("3500", a.getBalance().toPlainString());
                });
    }

    @Test
    @Order(8)
    void testDelete() {
        webTestClient.get().uri("/api/accounts")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Account.class)
                .hasSize(4);

        webTestClient.delete().uri(("/api/accounts/3"))
                .exchange()

                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        webTestClient.get().uri("/api/accounts")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Account.class)
                .hasSize(3);
        webTestClient.get().uri("/api/accounts/3")
                .exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}
