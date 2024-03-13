package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
