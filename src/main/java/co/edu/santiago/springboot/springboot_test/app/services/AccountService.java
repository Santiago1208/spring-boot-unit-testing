package co.edu.santiago.springboot.springboot_test.app.services;

import co.edu.santiago.springboot.springboot_test.app.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account findById(Long id);
    int getTotalTransfers(Long bankId);
    BigDecimal getBalance(Long id);
    void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount, Long bankId);
    List<Account> findAll();
    void save(Account account);
}
