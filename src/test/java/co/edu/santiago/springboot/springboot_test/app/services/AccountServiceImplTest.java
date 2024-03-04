package co.edu.santiago.springboot.springboot_test.app.services;

import co.edu.santiago.springboot.springboot_test.app.TestData;
import co.edu.santiago.springboot.springboot_test.app.repository.AccountRepository;
import co.edu.santiago.springboot.springboot_test.app.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {
    AccountRepository accountRepository;
    BankRepository bankRepository;
    AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        bankRepository = Mockito.mock(BankRepository.class);
        accountService = new AccountServiceImpl(accountRepository, bankRepository);
    }

    @Test
    @DisplayName("Returns the account balance properly")
    void givenAccountId_whenGetBalanceCalled_mustReturnCorrectBalance() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(TestData.ACCOUNT_001);
        Mockito.when(accountRepository.findById(2L)).thenReturn(TestData.ACCOUNT_002);

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());
    }

    @Test
    @DisplayName("Debits and Credits account balance properly")
    void givenAccountIdAndBankId_whenTransferCalledAndEnoughFunds_mustDebitAndCreditCorrectly() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(TestData.ACCOUNT_001);
        Mockito.when(accountRepository.findById(2L)).thenReturn(TestData.ACCOUNT_002);
        Mockito.when(bankRepository.findById(1L)).thenReturn(TestData.BANK);

        accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);
        assertEquals("900", sourceBalance.toPlainString());
        assertEquals("2100", targetBalance.toPlainString());
    }
}