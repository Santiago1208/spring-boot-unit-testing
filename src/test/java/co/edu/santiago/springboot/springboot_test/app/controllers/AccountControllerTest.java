package co.edu.santiago.springboot.springboot_test.app.controllers;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.dto.TransferDTO;
import co.edu.santiago.springboot.springboot_test.app.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.edu.santiago.springboot.springboot_test.app.TestData.createAccount001;
import static co.edu.santiago.springboot.springboot_test.app.TestData.createAccount002;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void findAllTest() {
        // Given
        List<Account> accounts = Arrays.asList(
                createAccount001().orElseThrow(),
                createAccount002().orElseThrow());
        when(accountService.findAll()).thenReturn(accounts);

        //When
        assertDoesNotThrow(() -> {
            mvc.perform(MockMvcRequestBuilders.get("/api/accounts/").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].owner").value("Santiago"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].owner").value("John"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance").value("1000"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].balance").value("2000"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                    .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(accounts)));
        });
        verify(accountService).findAll();
    }

    @Test
    void saveTest() {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));
        Mockito.doAnswer(invocation -> {
            Account toBeSaved = invocation.getArgument(0);
            toBeSaved.setId(3L); // Simulates the auto-generated ID
            return toBeSaved;
        }).when(accountService).save(any());

        // When
        assertDoesNotThrow(() -> {
            mvc.perform(MockMvcRequestBuilders
                    .post("/api/accounts/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(account)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/api/accounts/3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", Matchers.is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Account created successfully")));
        });

        verify(accountService).save(any());
    }
}
