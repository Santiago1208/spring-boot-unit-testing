package co.edu.santiago.springboot.springboot_test.app.repository;

import co.edu.santiago.springboot.springboot_test.app.models.Account;

import java.util.List;

public interface AccountRepository {
    List<Account> findAll();
    Account findById(Long id);
    void update(Account account);
}
