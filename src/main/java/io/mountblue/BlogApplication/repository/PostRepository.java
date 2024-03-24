package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import io.mountblue.BlogApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.isPublished = :isPublished WHERE p.id = :postId")
    void updateIsPublishedById(Long postId, boolean isPublished);
    Post findPostByTitleAndContent(String title, String content);

    List<Post> findByIsPublishedOrderByPublishedAtAsc(boolean isPublished);
    List<Post> findByIsPublishedOrderByPublishedAtDesc(boolean isPublished);

    List<Post> findPostByAuthorOrTags(User author, Tag tag);
    List<Post> findPostByIsPublished(boolean isPublished);
    Page<Post> findByIsPublished(boolean isPublished, Pageable pageable);
    List<Post> findByAuthor(User user);

    List<Post> findByTitleAndIsPublished(String title, boolean isPublished);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p IN :posts")
    Set<Post> findPostsByIsPublished(Set<Post> posts);

    @Query("SELECT p FROM Post p WHERE p.content LIKE %:content% AND p.isPublished = true")
    List<Post> findByContentContaining(@Param("content") String content);


    @Query("SELECT p FROM Post p WHERE p IN :posts ORDER BY p.publishedAt DESC")
    List<Post> findPostByPublishedAtDesc(List<Post> posts);

    @Query("SELECT p FROM Post p WHERE p IN :posts ORDER BY p.publishedAt ASC")
    List<Post> findPostByPublishedAtAsc(List<Post> posts);

    @Query("SELECT p FROM Post p WHERE p IN :posts ORDER BY LENGTH(TRIM(p.content)) ASC")
    List<Post> findPublishedPostsOrderByContentLengthAsc(List<Post> posts);

    @Query("SELECT p FROM Post p WHERE p IN :posts ORDER BY LENGTH(TRIM(p.content)) DESC")
    List<Post> findPublishedPostsOrderByContentLengthDesc(List<Post> posts);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t = ?1 AND p IN ?2 ORDER BY p.publishedAt DESC")
    List<Post> findByTagInPosts(Tag tag, List<Post> posts);

    @Query("SELECT p FROM Post p WHERE p IN :posts AND p.publishedAt BETWEEN :startDateTime AND :endDateTime")
    List<Post> findPostsInListAndBetweenDateTime(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("posts") List<Post> posts);

    List<Post> findPostByAuthorAndIsPublished(User user, boolean isPublished);

    @Query("SELECT MIN(publishedAt) FROM Post p WHERE p.isPublished = true")
    LocalDate findOldestPublishedDate();

    @Query("SELECT MAX(publishedAt) FROM Post p WHERE p.isPublished = true")
    LocalDate findNewestPublishedDate();
}
