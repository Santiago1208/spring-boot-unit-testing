package co.edu.santiago.springboot.springboot_test.app.controllers;

import co.edu.santiago.springboot.springboot_test.app.models.dto.TransferDTO;
import co.edu.santiago.springboot.springboot_test.app.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static co.edu.santiago.springboot.springboot_test.app.TestData.createAccount001;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    AccountService accountService;
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void findByIdTest() {
        // Given
        when(accountService.findById(1L)).thenReturn(createAccount001().orElseThrow());

        // When
        assertDoesNotThrow(() -> {
            mvc.perform(MockMvcRequestBuilders.get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                    // Then
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.owner").value("Santiago"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("1000"));
        });

        verify(accountService).findById(1L);
    }

    @Test
    void transferTest() {
        // Given
        TransferDTO dto = new TransferDTO(1L, 2L, new BigDecimal("100"), 1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer performed successfully");
        response.put("transaction", dto);

        // When
        assertDoesNotThrow(() -> {
            mvc.perform(MockMvcRequestBuilders
                    .post("/api/accounts/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dto)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Transfer performed successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.sourceAccountId").value(dto.sourceAccountId()))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(response)));
        });
    }
}