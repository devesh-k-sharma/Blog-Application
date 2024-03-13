package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
