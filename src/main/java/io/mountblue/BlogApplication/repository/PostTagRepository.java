package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
