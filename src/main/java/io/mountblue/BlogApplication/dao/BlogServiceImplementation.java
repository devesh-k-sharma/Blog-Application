package io.mountblue.BlogApplication.dao;

import io.mountblue.BlogApplication.entity.*;
import io.mountblue.BlogApplication.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Optional;

@Service
public class BlogServiceImplementation implements BlogService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public BlogServiceImplementation(UserRepository userRepository, PostRepository postRepository, TagRepository tagRepository, PostTagRepository postTagRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Post> showPage() {
        return postRepository.findAll();
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public List<Post> showAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public void publish(Long id, boolean isPublished) {
        postRepository.updateIsPublishedById(id, isPublished);
    }

    @Override
    public Post findPostByTitleAndContent(String title, String content) {
        return postRepository.findPostByTitleAndContent(title, content);
    }

    @Override
    public Page<Post> showAllPublishedBlogsPaged(boolean isPublished, Pageable pageable) {
        return postRepository.findByIsPublished(isPublished, pageable);
    }

    @Override
    public List<Post> showAllPublishedBlogs(boolean isPublished) {
        return postRepository.findPostByIsPublished(isPublished);
    }

    @Override
    public List<Post> showAllSearchBy(String searchFor) {
        Set<Post> posts = new HashSet<>();

        List<Post> postsByTitle = postRepository.findByTitleAndIsPublished(searchFor, true);
        if (!postsByTitle.isEmpty()) {
            posts.addAll(postsByTitle);
        }

        User user = userRepository.findByName(searchFor);
        if (user != null) {
            posts.addAll(user.getPosts());
        }

        Tag tag = tagRepository.findFirstByName(searchFor);
        if (tag != null) {
            List<PostTag> postTags = postTagRepository.findByTag(tag);
            for (PostTag postTag : postTags) {
                posts.add(postTag.getPost());
            }
        }

        List<Post> postByContent = postRepository.findByContentContaining(searchFor);
        if (!postByContent.isEmpty()) {
            posts.addAll(postByContent);
        }

        return new ArrayList<>(postRepository.findPostsByIsPublished(posts));
    }

    @Override
    public List<Post> filterBy(List<String> selectedAuthors, List<String> selectedTags, List<Post> posts) {
        List<Post> resultPosts = new ArrayList<>();
        if (selectedAuthors != null && selectedTags == null) {
            for (String authors : selectedAuthors) {
                User user = userRepository.findByName(authors);
                resultPosts.addAll(postRepository.findByAuthor(user));
            }
        } else if (selectedTags != null && selectedAuthors == null) {
            for (String tags : selectedTags) {
                Tag tag = tagRepository.findFirstByName(tags);
                resultPosts.addAll(postRepository.findByTagInPosts(tag, posts));
            }
        } else if (selectedTags != null && selectedAuthors != null) {
            User user = new User();
            Tag tag = new Tag();
            for (String authors : selectedAuthors) {
                user = userRepository.findByName(authors);
            }
            for (String tags : selectedTags) {
                tag = tagRepository.findFirstByName(tags);
            }
            resultPosts.addAll(postRepository.findPostByAuthorOrTags(user, tag));
        }
        return resultPosts;
    }


    @Override
    public List<Post> showAllSortBy(String sortType, List<Post> posts) {
        List<Post> resultPosts = new ArrayList<>();
        if (sortType.equals("newest")) {
            resultPosts.addAll(postRepository.findPostByPublishedAtDesc(posts));
        } else if (sortType.equals("oldest")) {
            resultPosts.addAll(postRepository.findPostByPublishedAtAsc(posts));
        } else if (sortType.equals("longest")) {
            resultPosts.addAll(postRepository.findPublishedPostsOrderByContentLengthDesc(posts));
        } else if (sortType.equals("shortest")) {
            resultPosts.addAll(postRepository.findPublishedPostsOrderByContentLengthAsc(posts));
        }
        return resultPosts;
    }

    @Override
    public Post getPostById(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);
    }

    @Override
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteComment(Long commentId, Long postId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        optionalComment.ifPresent(comment -> {
            if (comment.getPost().getId().equals(postId)) {
                commentRepository.delete(comment);
            } else {
                throw new IllegalArgumentException("No such comment");
            }
        });
    }

    @Override
    public Comment getComment(Long postId, Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Comment comments = null;
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getPost().getId().equals(postId)) {
                comments = comment;
            } else {
                throw new IllegalArgumentException("No such comment");
            }
        }
        return comments;
    }

    @Override
    public List<Post> features(String searchFor, String sortBy, List<String> selectedAuthors, List<String> selectedTags, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Post> posts = new ArrayList<>();
        if (searchFor != null && !searchFor.isEmpty()) {
            posts.addAll(showAllSearchBy(searchFor));
        } else {
            posts.addAll(showAllPublishedBlogs(true));
        }

        if (selectedAuthors != null || selectedTags != null) {
            posts = filterBy(selectedAuthors, selectedTags, posts);
        }

        if (startDateTime != null && endDateTime != null) {
            posts = postRepository.findPostsInListAndBetweenDateTime(startDateTime, endDateTime, posts);
        }

        Set<Post> set = new HashSet<>(posts);
        posts.clear();
        posts.addAll(set);
        if (sortBy != null && !sortBy.isEmpty()) {
            posts = showAllSortBy(sortBy, posts);
        }
        return posts;
    }

    @Override
    public LocalDate oldestPublishedDate() {
        return postRepository.findOldestPublishedDate();
    }

    @Override
    public LocalDate newestPublishedDate() {
        return postRepository.findNewestPublishedDate();
    }

    @Override
    public boolean userExists(String email) {
        return false;
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public boolean usernameExist(String username) {
        return false;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public List<Post> findPostByUsernameAndIsPublished(String username, boolean isPublished) {
        return null;
    }

}