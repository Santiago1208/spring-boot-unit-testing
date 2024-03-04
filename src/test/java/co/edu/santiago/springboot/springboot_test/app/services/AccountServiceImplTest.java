package co.edu.santiago.springboot.springboot_test.app.services;

import co.edu.santiago.springboot.springboot_test.app.TestData;
import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.Bank;
import co.edu.santiago.springboot.springboot_test.app.repository.AccountRepository;
import co.edu.santiago.springboot.springboot_test.app.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {
    AccountRepository accountRepository;
    BankRepository bankRepository;
    AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        bankRepository = mock(BankRepository.class);
        accountService = new AccountServiceImpl(accountRepository, bankRepository);
    }

    @Test
    @DisplayName("Returns the account balance properly")
    void givenAccountId_whenGetBalanceCalled_mustReturnCorrectBalance() {
        when(accountRepository.findById(1L)).thenReturn(TestData.ACCOUNT_001);
        when(accountRepository.findById(2L)).thenReturn(TestData.ACCOUNT_002);

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());
    }

    @Test
    @DisplayName("Debits and Credits account balance properly")
    void givenAccountIdAndBankId_whenTransferCalledAndEnoughFunds_mustDebitAndCreditCorrectly() {
        when(accountRepository.findById(1L)).thenReturn(TestData.ACCOUNT_001);
        when(accountRepository.findById(2L)).thenReturn(TestData.ACCOUNT_002);
        when(bankRepository.findById(1L)).thenReturn(TestData.BANK);

        accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);
        assertEquals("900", sourceBalance.toPlainString());
        assertEquals("2100", targetBalance.toPlainString());

        verify(accountRepository, times(3)).findById(1L);
        verify(accountRepository, times(3)).findById(2L);
        verify(accountRepository, times(2)).update(any(Account.class));
        verify(bankRepository).findById(1L);
        verify(bankRepository).update(any(Bank.class));
    }

    @Test
    @DisplayName("Increase the total transfers when the debit and credit is successful")
    void givenAccountIdAndBankId_whenTransferCalledAndEnoughFunds_mustIncreaseTotalTransfers() {
        when(accountRepository.findById(1L)).thenReturn(TestData.ACCOUNT_001);
        when(accountRepository.findById(2L)).thenReturn(TestData.ACCOUNT_002);
        when(bankRepository.findById(1L)).thenReturn(TestData.BANK);

        accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

        int totalTransfers = accountService.getTotalTransfers(1L);

        assertEquals(1, totalTransfers);
        verify(bankRepository, times(2)).findById(1L);
    }
}