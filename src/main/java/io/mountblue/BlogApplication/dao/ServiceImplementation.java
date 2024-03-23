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
public class ServiceImplementation {

    public ServiceImplementation() {}

    private UserRepository userRepository;
    private PostRepository postRepository;
    private TagRepository tagRepository;
    private PostTagRepository postTagRepository;
    private CommentRepository commentRepository;

    @Autowired
    public ServiceImplementation(UserRepository userRepository, PostRepository postRepository, TagRepository tagRepository, PostTagRepository postTagRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> showPage() {
        List<Post> postDBData = postRepository.findAll();
        return postDBData;
    }

    public LocalDate oldestDate() {
        List<Post> posts = postRepository.findByIsPublishedOrderByPublishedAtAsc(true);
        if (!posts.isEmpty()) {
            return posts.getFirst().getPublishedAt().toLocalDate();
        } else {
            return null;
        }
    }

    public LocalDate newestDate() {
        List<Post> posts = postRepository.findByIsPublishedOrderByPublishedAtDesc(true);
        if (!posts.isEmpty()) {
            return posts.getFirst().getPublishedAt().toLocalDate();
        } else {
            return null;
        }
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public List<Post> showAllPosts() {
        List<Post> allPosts=postRepository.findAll();
        return allPosts;
    }
    public void publish(Long id, boolean isPublished) {
        postRepository.updateIsPublishedById(id, isPublished);
    }

    public Post findPostByTitleAndContent(String title, String content) {
        return postRepository.findPostByTitleAndContent(title, content);
    }

    public Page<Post> showAllPublishedBlogsPaged(boolean isPublished, Pageable pageable) {
        return postRepository.findByIsPublished(isPublished, pageable);
    }
    public List<Post> showAllPublishedBlogs(boolean isPublished) {
        return postRepository.findPostByIsPublished(isPublished);
    }

//    public List<Post> showAllSortBy(String sortType) {
//        if(sortType.equals("newest")){
//            return postRepository.findPostByIsPublishedOrderByPublishedAtDesc(true);
//        }
//        else if (sortType.equals("oldest")) {
//            return postRepository.findPostByIsPublishedOrderByPublishedAtAsc(true);
//        }
//        else if (sortType.equals("longest")) {
//            return postRepository.findPublishedPostsOrderByContentLengthDesc();
//        }
//        else if (sortType.equals("shortest")) {
//            return postRepository.findPublishedPostsOrderByContentLengthAsc();
//        }
//        else {
//            return new ArrayList<>();
//        }
//    }
//
    public List<Post> showAllSearchBy(String searchFor) {
        Set<Post> posts = new HashSet<>();

        List<Post> postsByTitle = postRepository.findByTitleAndIsPublished(searchFor, true);
        if (!postsByTitle.isEmpty()) {
            //return postsByTitle;
            posts.addAll(postsByTitle);
        }

        User user = userRepository.findByName(searchFor);
        if (user != null) {
            posts.addAll(user.getPosts());
            //return posts;
        }

        Tag tag = tagRepository.findFirstByName(searchFor);
        if (tag != null) {
            List<PostTag> postTags = postTagRepository.findByTag(tag);
            for (PostTag postTag : postTags) {
                posts.add(postTag.getPost());
            }
            //return posts;
        }

        List<Post> postByContent = postRepository.findByContentContaining(searchFor);
        if(!postByContent.isEmpty()) {
            //return postByContent;
            posts.addAll(postByContent);
        }
        List<Post> resultPosts = new ArrayList<>(posts);
        return resultPosts;
    }

    public List<Post> filterBy(List<String> selectedAuthors, List<String> selectedTags, List<Post> posts) {
        List<Post> resultPosts = new ArrayList<>();
        if(selectedAuthors != null && selectedTags == null) {
            for(String authors : selectedAuthors) {
                User user = userRepository.findByName(authors);
                resultPosts.addAll(postRepository.findByAuthor(user));
            }
        }
        else if (selectedTags != null && selectedAuthors == null) {
            for(String tags: selectedTags) {
                Tag tag = tagRepository.findFirstByName(tags);
                resultPosts.addAll(postRepository.findByTagInPosts(tag, posts));
            }
        } else if (selectedTags != null && selectedAuthors != null) {
            User user = new User();
            Tag tag = new Tag();
            for(String authors : selectedAuthors) {
                user = userRepository.findByName(authors);
            }
            for(String tags: selectedTags) {
                tag = tagRepository.findFirstByName(tags);
            }
            resultPosts.addAll(postRepository.findPostByAuthorOrTags(user, tag));
        }
        return resultPosts;
    }


    public List<Post> showAllSortBy(String sortType, List<Post> posts) {

        List<Post> resultPosts = new ArrayList<>();
            if(sortType.equals("newest")){
                resultPosts.addAll(postRepository.findPostByIsPublishedOrderByPublishedAtDesc(true, posts));
                return resultPosts;
            }
            else if (sortType.equals("oldest")) {
                resultPosts.addAll(postRepository.findPostByIsPublishedOrderByPublishedAtAsc(true, posts));
                return resultPosts;
            }
            else if (sortType.equals("longest")) {
                resultPosts.addAll(postRepository.findPublishedPostsOrderByContentLengthDesc(posts));
                return resultPosts;
            }
            else if (sortType.equals("shortest")) {
                resultPosts.addAll(postRepository.findPublishedPostsOrderByContentLengthAsc(posts));
                return resultPosts;
            }
            else {
                return resultPosts;
            }
    }

//    public List<Post> multipleFeatures(String searchFor, String sortType, List<String> selectedAuthors, List<String> selectedTags) {
//        List<Post> posts = new ArrayList<>();
//        if(!searchFor.isEmpty()) {
//            posts = showAllSearchBy(searchFor);
//        }
//        List<Post> resultPosts = new ArrayList<>();
//        if(sortType != null) {
//            if(sortType.equals("newest")){
//                resultPosts.addAll(postRepository.findPostByIsPublishedOrderByPublishedAtDesc(true, posts));
//                return resultPosts;
//            }
//            else if (sortType.equals("oldest")) {
//                resultPosts.addAll(postRepository.findPostByIsPublishedOrderByPublishedAtAsc(true, posts));
//                return resultPosts;
//            }
//            else if (sortType.equals("longest")) {
//                resultPosts.addAll(postRepository.findPublishedPostsOrderByContentLengthDesc(posts));
//                return resultPosts;
//            }
//            else if (sortType.equals("shortest")) {
//                resultPosts.addAll(postRepository.findPublishedPostsOrderByContentLengthAsc(posts));
//                return resultPosts;
//            }
//            else {
//                return resultPosts;
//            }
//
//        }
//        return posts;
//    }

    public Post getPostById(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    public List<Tag> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags;
    }

    public Tag saveTag(Tag tag) {
        Tag tags = tagRepository.save(tag);
        return tags;
    }

    public void deleteComment(Long commentId, Long postId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getPost().getId().equals(postId)) {
                commentRepository.delete(comment);
            } else {
                throw new IllegalArgumentException("No such comment");
            }
        }
    }

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

    static String search = "";

    public List<Post> features(String searchFor, String sortBy, List<String> selectedAuthors, List<String> selectedTags, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Post> posts = new ArrayList<>();
        if (searchFor != null && !searchFor.isEmpty() && !searchFor.equals(search)) {
            posts.addAll(showAllSearchBy(searchFor));
            search = searchFor;
            return posts;
        } else {
            posts.addAll(postRepository.findAll());
        }

        if (selectedAuthors != null || selectedTags != null) {
            posts = filterBy(selectedAuthors, selectedTags, posts);
        }

        if (sortBy != null) {
            posts = showAllSortBy(sortBy, posts);
        }

        if (startDateTime != null && endDateTime != null) {
            posts = postRepository.findPostsInListAndBetweenDateTime(startDateTime, endDateTime, posts);
        }

        Set<Post> set = new HashSet<>(posts);
        return new ArrayList<>(set);
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean usernameExist(String username) {
        return userRepository.existsByUsername(username);
    }
}
