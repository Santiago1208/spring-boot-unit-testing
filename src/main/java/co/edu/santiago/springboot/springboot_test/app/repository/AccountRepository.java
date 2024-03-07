package co.edu.santiago.springboot.springboot_test.app.repository;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select acc from Account acc where acc.owner ilike ?1%")
    Optional<Account> findByOwnerStartingWith(String ownerName);
}
