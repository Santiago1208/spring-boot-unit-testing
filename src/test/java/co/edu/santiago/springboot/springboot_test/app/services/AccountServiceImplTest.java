package co.edu.santiago.springboot.springboot_test.app.services;

import co.edu.santiago.springboot.springboot_test.app.TestData;
import co.edu.santiago.springboot.springboot_test.app.exception.InsufficientFundsException;
import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.Bank;
import co.edu.santiago.springboot.springboot_test.app.repository.AccountRepository;
import co.edu.santiago.springboot.springboot_test.app.repository.BankRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    BankRepository bankRepository;
    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    @DisplayName("Returns the account balance properly")
    void givenAccountId_whenGetBalanceCalled_mustReturnCorrectBalance() {
        when(accountRepository.findById(1L)).thenReturn(TestData.createAccount001());
        when(accountRepository.findById(2L)).thenReturn(TestData.createAccount002());

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());
    }

    @Test
    @DisplayName("Debits and Credits account balance properly")
    void givenAccountIdAndBankId_whenTransferCalledAndEnoughFunds_mustDebitAndCreditCorrectly() {
        when(accountRepository.findById(1L)).thenReturn(TestData.createAccount001());
        when(accountRepository.findById(2L)).thenReturn(TestData.createAccount002());
        when(bankRepository.findById(1L)).thenReturn(TestData.createBank());

        accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);
        assertEquals("900", sourceBalance.toPlainString());
        assertEquals("2100", targetBalance.toPlainString());

        verify(accountRepository, times(2)).findById(1L);
        verify(accountRepository, times(2)).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(bankRepository).findById(1L);
        verify(bankRepository).save(any(Bank.class));

        // Testing the most context possible
        verify(accountRepository, never()).findAll();
        verify(accountRepository, times(4)).findById(anyLong());
    }

    @Test
    @DisplayName("Increase the total transfers when the debit and credit is successful")
    void givenAccountIdAndBankId_whenTransferCalledAndEnoughFunds_mustIncreaseTotalTransfers() {
        when(accountRepository.findById(1L)).thenReturn(TestData.createAccount001());
        when(accountRepository.findById(2L)).thenReturn(TestData.createAccount002());
        when(bankRepository.findById(1L)).thenReturn(TestData.createBank());

        accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

        int totalTransfers = accountService.getTotalTransfers(1L);

        assertEquals(1, totalTransfers);
        verify(bankRepository, times(2)).findById(1L);

        // Testing the most context possible
        verify(accountRepository, never()).findAll();
    }

    @Test
    @DisplayName("Throws InsufficientFundsException when no funds to debit and credit")
    void givenAccountIdAndBankId_whenTransferCalledAndInsufficientFunds_mustThrowException() {
        when(accountRepository.findById(1L)).thenReturn(TestData.createAccount001());
        when(accountRepository.findById(2L)).thenReturn(TestData.createAccount002());
        BigDecimal amountToTransfer = new BigDecimal("1200");

        assertThrows(InsufficientFundsException.class,
                () -> accountService.transfer(1L, 2L, amountToTransfer, 1L));

        BigDecimal sourceBalance = accountService.getBalance(1L);
        BigDecimal targetBalance = accountService.getBalance(2L);
        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        verify(accountRepository, times(2)).findById(1L);
        verify(accountRepository, times(1)).findById(2L);
        verify(accountRepository, never()).save(any(Account.class));

        // Testing the most context possible
        verify(accountRepository, never()).findAll();
        verify(accountRepository, times(3)).findById(anyLong());
    }

    @Test
    @DisplayName("Returns the same account when findById called twice. (No concurrent writing happening)")
    void givenAccountId_whenFindByIdCalledTwice_itMustReturnTheSameAccount() {
        when(accountRepository.findById(1L)).thenReturn(TestData.createAccount001());

        Account account1 = accountService.findById(1L);
        Account account2 = accountService.findById(1L);

        assertSame(account1, account2);
        assertEquals("Santiago", account1.getOwner());
        assertEquals("Santiago", account2.getOwner());

        verify(accountRepository, times(2)).findById(1L);
    }

    @Test
    void findAllTest() {
        // Given
        List<Account> accounts = Arrays.asList(
                TestData.createAccount001().orElseThrow(),
                TestData.createAccount002().orElseThrow());
        when(accountRepository.findAll()).thenReturn(accounts);

        // When
        List<Account> actualAccounts = accountService.findAll();

        // Then
        assertFalse(actualAccounts.isEmpty());
        assertEquals(2, actualAccounts.size());
        assertTrue(actualAccounts.contains(TestData.createAccount002().orElseThrow()));

        verify(accountRepository).findAll();
    }
}