package co.edu.santiago.springboot.springboot_test.app.controllers;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.dto.TransferDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerWTCTest {

    @Autowired
    WebTestClient webClient;
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("It transfers amounts between accounts (Rest Integration)")
    @Order(1)
    void transferTest() {
        // Given
        TransferDTO transferDTO = new TransferDTO(1L, 2L, new BigDecimal("100"), 1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer performed successfully");
        response.put("transaction", transferDTO);

        // When
        assertDoesNotThrow(() -> {
            webClient
                    .post()
                    .uri("/api/accounts/transfer")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(transferDTO)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody() // By default, it returns byte[]. You can call expectBody(String.class) to get the String return, but it won't be compatible with jsonPath()
                    .consumeWith(serverResponse -> { // Use this for the case you used expectBody(String.class)
                        try {
                            byte[] body = serverResponse.getResponseBody();
                            JsonNode json = mapper.readTree(body);
                            assertEquals("Transfer performed successfully", json.path("message").asText());
                            assertEquals(1L, json.path("transaction").path("sourceAccountId").asLong());
                            assertEquals("100", json.path("transaction").path("amount").asText());
                            assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .jsonPath("$.message").isNotEmpty() // Or use this when using the default byte[]
                    .jsonPath("$.message").isEqualTo("Transfer performed successfully")
                    .jsonPath("$.transaction.sourceAccountId").isEqualTo(transferDTO.sourceAccountId())
                    .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                    .json(mapper.writeValueAsString(response));
        });
    }

    @Test
    @Order(2)
    void findByIdJsonPathTest() {
        Account account = new Account(1L, "Santiago", new BigDecimal("900"));
        assertDoesNotThrow(() -> {
            webClient
                    .get()
                    .uri("/api/accounts/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.owner").isEqualTo("Santiago")
                    .jsonPath("$.balance").isEqualTo(900)
                    .json(mapper.writeValueAsString(account));
        });
    }

    @Test
    @Order(3)
    void findByIdConsumeWithTest() {
        assertDoesNotThrow(() -> {
            webClient
                    .get()
                    .uri("/api/accounts/2")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody(Account.class)
                    .consumeWith(serverResponse -> {
                        Account account = serverResponse.getResponseBody();
                        assertNotNull(account);
                        assertEquals("John", account.getOwner());
                        assertEquals("2100.00", account.getBalance().toPlainString());
                    });
        });
    }

    @Test
    @Order(4)
    void findAllJsonPathTest() {
        assertDoesNotThrow(() -> {
            webClient
                    .get()
                    .uri("/api/accounts/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$[0].owner").isEqualTo("Santiago")
                    .jsonPath("$[0].id").isEqualTo(1)
                    .jsonPath("$[0].balance").isEqualTo(900)
                    .jsonPath("$[1].owner").isEqualTo("John")
                    .jsonPath("$[1].id").isEqualTo(2)
                    .jsonPath("$[1].balance").isEqualTo(2100)
                    .jsonPath("$").isArray()
                    .jsonPath("$").value(hasSize(2));
        });
    }

    @Test
    @Order(5)
    void findAllConsumeWithTest() {
        assertDoesNotThrow(() -> {
            webClient
                    .get()
                    .uri("/api/accounts/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBodyList(Account.class)
                    .consumeWith(serverResponse -> {
                        List<Account> accounts = serverResponse.getResponseBody();

                        assertNotNull(accounts);
                        assertFalse(accounts.isEmpty());
                        assertEquals(2, accounts.size());

                        assertEquals(1L, accounts.get(0).getId());
                        assertEquals("Santiago", accounts.get(0).getOwner());
                        assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
                        assertEquals(2L, accounts.get(1).getId());
                        assertEquals("John", accounts.get(1).getOwner());
                        assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());
                    });
        });
    }

    @Test
    @Order(6)
    void saveJsonPathTest() {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        assertDoesNotThrow(() -> {
            webClient
                    .post()
                    .uri("/api/accounts/")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(account)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                    .jsonPath("$.status").isEqualTo("Created")
                    .jsonPath("$.message").isEqualTo("Account created successfully");
        });
    }

    @Test
    @Order(7)
    void saveConsumeWithTest() {
        // Given
        Account account = new Account(null, "Pepa", new BigDecimal("3500"));

        // When
        assertDoesNotThrow(() -> {
            webClient
                    .post()
                    .uri("/api/accounts/")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(account)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .consumeWith(serverResponse -> {
                        byte[] body = serverResponse.getResponseBody();
                        try {
                            JsonNode json = mapper.readTree(body);
                            assertEquals(LocalDate.now().toString(), json.path("date").asText());
                            assertEquals("Created", json.path("status").asText());
                            assertEquals("Account created successfully", json.path("message").asText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }
}

