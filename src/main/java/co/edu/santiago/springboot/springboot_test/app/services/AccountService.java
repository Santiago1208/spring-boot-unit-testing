package co.edu.santiago.springboot.springboot_test.app.services;

import co.edu.santiago.springboot.springboot_test.app.models.Account;

import java.math.BigDecimal;

public interface AccountService {
    Account findById(Long id);
    int getTotalTransfers(Long bankId);
    BigDecimal getBalance(Long id);
    void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount);
}
