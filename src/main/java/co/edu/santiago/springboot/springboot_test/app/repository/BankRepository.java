package co.edu.santiago.springboot.springboot_test.app.repository;

import co.edu.santiago.springboot.springboot_test.app.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
}
