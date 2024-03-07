package co.edu.santiago.springboot.springboot_test.app.repositories;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountJpaTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("Returns the account with the provided ID")
    void givenAccountID_whenFindByIdCalled_mustReturnCorrespondingAccount() {
        Optional<Account> account = accountRepository.findById(1L);

        assertTrue(account.isPresent());
        assertEquals("Santiago", account.orElseThrow().getOwner());
    }

    @Test
    @DisplayName("Returns the account with a partial owner name")
    void givenAccountOwner_whenFindByOwnerStartingWithCalled_mustReturnCorrespondingAccount() {
        Optional<Account> account = accountRepository.findByOwnerStartingWith("San");

        assertTrue(account.isPresent());
        assertEquals("Santiago", account.orElseThrow().getOwner());
        assertEquals("1000.00", account.orElseThrow().getBalance().toPlainString());
    }

    @Test
    @DisplayName("An exception is thrown when no owner name starts with the provided string")
    void givenNonExistingAccountOwner_whenFindByOwnerStartingWithCalled_mustThrowAnException() {
        Optional<Account> account = accountRepository.findByOwnerStartingWith("Car");
        assertThrows(NoSuchElementException.class, account::orElseThrow);
    }

    @Test
    @DisplayName("Returns all the accounts")
    void whenFindAllCalled_mustReturnAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    @DisplayName("Saves the specified account")
    void givenNewAccount_whenSaveCalled_mustNotThrowAnyException() {
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));
        assertDoesNotThrow(() -> accountRepository.save(account));

        Account pepeAccount = accountRepository.findByOwnerStartingWith("Pepe").orElseThrow();
        assertEquals("Pepe", pepeAccount.getOwner());
        assertEquals("3000", pepeAccount.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Updates an existent account")
    void givenAccountToUpdate_whenSaveCalled_mustNotThrowAnyException() {
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));
        accountRepository.save(account);

        account.setBalance(new BigDecimal("4000"));
        accountRepository.save(account);

        Account pepeAccount = accountRepository.findByOwnerStartingWith("Pepe").orElseThrow();
        assertEquals("Pepe", pepeAccount.getOwner());
        assertEquals("4000", pepeAccount.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Deletes an specified account")
    void givenAccountId_whenDeleteCalled_noRecordShouldExist() {
        Account johnAccount = accountRepository.findById(2L).orElseThrow();

        accountRepository.delete(johnAccount);

        Optional<Account> deletedAccount = accountRepository.findByOwnerStartingWith("John");
        assertThrows(NoSuchElementException.class, deletedAccount::orElseThrow);
    }
}
