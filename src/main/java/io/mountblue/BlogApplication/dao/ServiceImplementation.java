package io.mountblue.BlogApplication.dao;

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

    public void updatePost(Long postId, String title, String content, String excerpt, String[] tagNames) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(title);
            post.setContent(content);
            post.setExcerpt(excerpt);

            List<Tag> tags = new ArrayList<>();
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tag = tagRepository.save(tag);
                }
                tags.add(tag);
            }
            post.setTags(tags);

            postRepository.save(post);
        } else {
            System.out.println("ERROR");
        }
    }

}
