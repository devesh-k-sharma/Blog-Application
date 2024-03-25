package io.mountblue.BlogApplication.restcontroller;

import io.mountblue.BlogApplication.dao.BlogService;
import io.mountblue.BlogApplication.entity.Comment;
import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import io.mountblue.BlogApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BlogRestController {

    private final BlogService serviceImplementation;

    public BlogRestController(BlogService serviceImplementation) {
        this.serviceImplementation = serviceImplementation;
    }

//    @GetMapping("/posts")
//    public ResponseEntity<List<Post>> getAllPosts() {
//        List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
//        return new ResponseEntity<>(posts, HttpStatus.OK);
//    }

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = serviceImplementation.showAllPublishedBlogsPaged(true, pageable);
        return new ResponseEntity<>(postsPage, HttpStatus.OK);
    }

    @GetMapping("/user")
    public User showUser(@AuthenticationPrincipal UserDetails user) {
        if(user != null) {
            User author = serviceImplementation.findUserByUsername(user.getUsername());
            return author;
        }
        return null;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Post post = serviceImplementation.getPostById(postId);
        if (post != null) {
            return new ResponseEntity<>(post, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/createPost")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @RequestParam(value = "tags") String tag,
                           @AuthenticationPrincipal UserDetails userDetails) {
        post.setPublished_at(LocalDateTime.now());
        String paragraph = post.getContent();
        String[] words = paragraph.split("\\s+");
        int maxLength = Math.min(words.length, 30);
        String excerpt = String.join(" ", Arrays.copyOf(words, maxLength));
        post.setExcerpt(excerpt);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User author = serviceImplementation.findUserByUsername(username);
        System.out.println(author);
/*        author.setRole("Author");*/
        post.setAuthor(author);

        String[] tagNames = tag.split(",");
        Map<String, Tag> existingTagsMap = new HashMap<>();
        List<Tag> existingTags = serviceImplementation.getAllTags();
        for (Tag existingTag : existingTags) {
            existingTagsMap.put(existingTag.getName(), existingTag);
        }
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag existingTag = existingTagsMap.get(tagName.trim());
            if (existingTag != null) {
                tags.add(existingTag);
            } else {
                Tag newTag = new Tag();
                newTag.setName(tagName.trim());
                Tag savedTag = serviceImplementation.saveTag(newTag);
                tags.add(savedTag);
            }
        }
        post.setTags(tags);
        System.out.println(post);
        serviceImplementation.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping("/updatePost/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Post updatedPost, @RequestParam(value = "tags") String tag) {
        Post existingPost = serviceImplementation.getPostById(postId);

        if (existingPost != null) {
            updatedPost.setId(postId);
            updatedPost.setPublished_at(existingPost.getPublished_at());

            String paragraph = updatedPost.getContent();
            String[] words = paragraph.split("\\s+");
            int maxLength = Math.min(words.length, 30);
            String excerpt = String.join(" ", Arrays.copyOf(words, maxLength));
            updatedPost.setExcerpt(excerpt);

            String[] tagNames = tag.split(",");
            Map<String, Tag> existingTagsMap = new HashMap<>();
            List<Tag> existingTags = serviceImplementation.getAllTags();
            for (Tag existingTag : existingTags) {
                existingTagsMap.put(existingTag.getName(), existingTag);
            }
            List<Tag> tags = new ArrayList<>();
            for (String tagName : tagNames) {
                Tag existingTag = existingTagsMap.get(tagName.trim());
                if (existingTag != null) {
                    tags.add(existingTag);
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(tagName.trim());
                    Tag savedTag = serviceImplementation.saveTag(newTag);
                    tags.add(savedTag);
                }
            }
            updatedPost.setTags(tags);
            serviceImplementation.save(updatedPost);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        Post existingPost = serviceImplementation.getPostById(postId);
        if (existingPost != null) {
            serviceImplementation.delete(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam(value = "query") String query) {
        List<Post> posts = serviceImplementation.showAllSearchBy(query);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/filter/{tagName}/{authorName}")
    public ResponseEntity<List<Post>> getPostsByTag(@PathVariable List<String> tagName, @PathVariable List<String> authorName) {
        List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
        List <Post> resultPosts = serviceImplementation.filterBy(authorName, tagName, posts);
        return new ResponseEntity<>(resultPosts, HttpStatus.OK);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Post>> getPostsByDateRange(@RequestParam(value = "query") String sortType) {
        List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
        List<Post> resultPosts = serviceImplementation.showAllSortBy(sortType, posts);
        return new ResponseEntity<>(resultPosts, HttpStatus.OK);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> showCommentsOnPost(@PathVariable Long postId) {
        Post post = serviceImplementation.getPostById(postId);
        List<Comment> comments = post.getComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }



    @PostMapping("/{postId}/addComment")
    public ResponseEntity<Post> addComment(@RequestBody Comment comment, @PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Post post = serviceImplementation.getPostById(postId);

        if (authentication != null && authentication.isAuthenticated()) {
            User user = serviceImplementation.findUserByUsername(authentication.getName());
            comment.setName(user.getName());
            comment.setEmail(user.getEmail());
        } else {
            comment.setName("Anonymous");
        }
        comment.setPost(post);
        post.getComments().add(comment);
        serviceImplementation.save(post);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
