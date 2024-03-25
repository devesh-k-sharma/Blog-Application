package io.mountblue.BlogApplication.dao;

import io.mountblue.BlogApplication.entity.Comment;
import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import io.mountblue.BlogApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BlogService {

    List<Post> showPage();

    void save(Post post);

    Post savePost(Post post);

    List<Post> showAllPosts();

    void publish(Long id, boolean isPublished);

    Post findPostByTitleAndContent(String title, String content);

    Page<Post> showAllPublishedBlogsPaged(boolean isPublished, Pageable pageable);

    List<Post> showAllPublishedBlogs(boolean isPublished);

    List<Post> showAllSearchBy(String searchFor);

    List<Post> filterBy(List<String> selectedAuthors, List<String> selectedTags, List<Post> posts);

    List<Post> showAllSortBy(String sortType, List<Post> posts);

    Post getPostById(Long id);

    void delete(Long id);

    List<Tag> getAllTags();

    Tag saveTag(Tag tag);

    void deleteComment(Long commentId, Long postId);

    Comment getComment(Long postId, Long commentId);

    List<Post> features(String searchFor, String sortBy, List<String> selectedAuthors, List<String> selectedTags, LocalDateTime startDateTime, LocalDateTime endDateTime);

    LocalDate oldestPublishedDate();

    LocalDate newestPublishedDate();

    boolean userExists(String email);

    void saveUser(User user);

    boolean usernameExist(String username);

    User findUserByUsername(String username);

    List<Post> findPostByUsernameAndIsPublished(String username, boolean isPublished);


    void updateComment(Comment comment);
}
