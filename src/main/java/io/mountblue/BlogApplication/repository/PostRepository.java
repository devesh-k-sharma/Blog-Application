package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Transactional
    @Query("INSERT INTO Post(is_published) VALUES (?1)")
    void saveWithIsPublishedFalse(Post post);

    @Modifying
    @Transactional
    @Query("INSERT INTO Post(is_published) VALUES (?1)")
    void saveWithIsPublishedTrue(Post post);
}
