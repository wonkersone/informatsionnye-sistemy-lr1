package ru.itmo.vehiclelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.vehiclelab.domain.UserAccount;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
}
