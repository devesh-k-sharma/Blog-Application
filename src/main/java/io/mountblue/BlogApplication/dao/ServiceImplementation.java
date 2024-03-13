package io.mountblue.BlogApplication.dao;

import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.repository.*;
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

    public List<Post> showPage() {
        List<Post> postDBData = postRepository.findAll();
        return postDBData;
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public void saveWithIsPublishedFalse(Post post) {
        postRepository.saveWithIsPublishedFalse(post);
    }

    public void saveWithIsPublishedTrue(Post post) {
        postRepository.saveWithIsPublishedTrue(post);
    }

}
