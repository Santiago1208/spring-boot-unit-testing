package co.edu.santiago.springboot.springboot_test.app;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.Bank;

import java.math.BigDecimal;

public class TestData {
    public static final Account ACCOUNT_001 = new Account(1L, "Santiago", new BigDecimal("1000"));
    public static final Account ACCOUNT_002 = new Account(2L, "John", new BigDecimal("2000"));
    public static final Bank BANK = new Bank(1L, "Bank of America", 0);
}
