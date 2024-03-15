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

    List<Post> findPostByIsPublishedOrderByPublishedAtDesc(boolean isPublished);

    List<Post> findPostByIsPublishedOrderByPublishedAtAsc(boolean isPublished);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true ORDER BY LENGTH(p.content) ASC")
    List<Post> findPublishedPostsOrderByContentLengthAsc();

    @Query("SELECT p FROM Post p WHERE p.isPublished = true ORDER BY LENGTH(p.content) DESC")
    List<Post> findPublishedPostsOrderByContentLengthDesc();

    List<Post> findByTitle(String title);

    @Query("SELECT p FROM Post p WHERE p.content LIKE %:content%")
    List<Post> findByContentContaining(@Param("content") String content);
}
