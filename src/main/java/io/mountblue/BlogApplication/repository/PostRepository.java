package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.isPublished = :isPublished WHERE p.id = :postId")
    void updateIsPublishedById(Long postId, boolean isPublished);
    Post findPostByTitleAndContent(String title, String content);
    List<Post> findPostByIsPublished(boolean isPublished);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.title = :title, p.content = :content, p.excerpt = :excerpt, p.tags = :tags WHERE p.id = :postId")
    void updatePost(@Param("postId") Long postId, @Param("title") String title, @Param("content") String content, @Param("excerpt") String excerpt, @Param("tags") List<Tag> tags);

}
