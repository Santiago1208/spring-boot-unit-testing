package co.edu.santiago.springboot.springboot_test.app;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.Bank;

import java.math.BigDecimal;

public class TestData {
    public static Account createAccount001() {
        return new Account(1L, "Santiago", new BigDecimal("1000"));
    }

    public static Account createAccount002() {
        return new Account(2L, "John", new BigDecimal("2000"));
    }

    public static Bank createBank() {
        return new Bank(1L, "Bank of America", 0);
    }
}
