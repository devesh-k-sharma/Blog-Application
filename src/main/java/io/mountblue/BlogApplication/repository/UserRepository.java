package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
