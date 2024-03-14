package io.mountblue.BlogApplication.dao;

import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import io.mountblue.BlogApplication.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void saveTag(Tag tag) {
        tagRepository.save(tag);
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

}
