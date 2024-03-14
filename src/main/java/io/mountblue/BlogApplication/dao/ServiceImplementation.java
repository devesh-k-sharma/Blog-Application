package io.mountblue.BlogApplication.dao;

import io.mountblue.BlogApplication.entity.Comment;
import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import io.mountblue.BlogApplication.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
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

    public List<Post> showAllPublishedBlogs(boolean isPublished) {
        return postRepository.findPostByIsPublished(isPublished);
    }

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

}
