package co.edu.santiago.springboot.springboot_test.app;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.Bank;

import java.math.BigDecimal;
import java.util.Optional;

public class TestData {
    public static Optional<Account> createAccount001() {
        return Optional.of(new Account(1L, "Santiago", new BigDecimal("1000")));
    }

    public static Optional<Account> createAccount002() {
        return Optional.of(new Account(2L, "John", new BigDecimal("2000")));
    }

    public static Optional<Bank> createBank() {
        return Optional.of(new Bank(1L, "Bank of America", 0));
    }
}
