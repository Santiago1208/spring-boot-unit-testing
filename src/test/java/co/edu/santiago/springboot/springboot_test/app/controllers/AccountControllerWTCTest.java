package co.edu.santiago.springboot.springboot_test.app.controllers;

import co.edu.santiago.springboot.springboot_test.app.models.dto.TransferDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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
                    .uri(URI.create("http://localhost:8080/api/accounts/transfer"))
                    .contentType(APPLICATION_JSON)
                    .bodyValue(transferDTO)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.message").isNotEmpty()
                    .jsonPath("$.message").isEqualTo("Transfer performed successfully")
                    .jsonPath("$.transaction.sourceAccountId").isEqualTo(transferDTO.sourceAccountId())
                    .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                    .json(mapper.writeValueAsString(response));
        });
    }
}
