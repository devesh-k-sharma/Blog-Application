package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    boolean existsByEmail(String email);

    //Optional<User> findByNames(String username);

    boolean existsByUsername(String username);
}
