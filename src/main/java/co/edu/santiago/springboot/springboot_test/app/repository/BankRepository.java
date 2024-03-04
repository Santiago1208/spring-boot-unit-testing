package co.edu.santiago.springboot.springboot_test.app.repository;

import co.edu.santiago.springboot.springboot_test.app.models.Bank;

import java.util.List;

public interface BankRepository {
    List<Bank> findAll();
    Bank findById(Long id);
    void update(Bank bank);

}
