package io.mountblue.BlogApplication.controller;

import io.mountblue.BlogApplication.dao.ServiceImplementation;
import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class BlogController {

    private ServiceImplementation serviceImplementation;

    public BlogController(ServiceImplementation serviceImplementation){
        this.serviceImplementation = serviceImplementation;
    }

    @GetMapping("/")
    public String showPage(Model model) {
        List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
        model.addAttribute("posts", posts);
        return "all-blogs";
    }

    @GetMapping("/newpost")
    public String newPost(Model model) {
        model.addAttribute("form", new Post());
        return "new-post";
    }

    @PostMapping("/action")
    public String saveForm(@ModelAttribute("form") Post post, @RequestParam String action, @RequestParam("tagList") String tagsString) {
        if ("Publish".equals(action)) {
            post.setPublished_at(LocalDateTime.now());
            Post existingPost = serviceImplementation.findPostByTitleAndContent(post.getTitle(), post.getContent());
            if (existingPost != null) {
                serviceImplementation.publish(existingPost.getId(), true);
                return "redirect:/";
            }
            post.setIs_published(true);
        }
        else if ("Save".equals(action)) {
            post.setIs_published(false);
        }
        String paragraph = post.getContent();
        String[] words = paragraph.split("\\s+");
        int maxLength = Math.min(words.length, 20);
        String excerpt = String.join(" ", Arrays.copyOf(words, maxLength));
        post.setExcerpt(excerpt);
        String[] tagNames = tagsString.split(",");
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tags.add(tag);
        }
        post.setTags(tags);
        serviceImplementation.save(post);
        return "redirect:/";
    }

    @GetMapping("/post{postId}")
    public String viewBlogPost(@PathVariable Long postId, Model model) {
        Post post = serviceImplementation.getPostById(postId);
        model.addAttribute("post", post);
        return "blogpost";
    }

    @GetMapping("/update{postId}")
    public String updateById(@PathVariable Long postId, Model model) {
        Post post = serviceImplementation.getPostById(postId);
        model.addAttribute("form", post);
        return "new-post";
    }

    @GetMapping("/delete{postId}")
    public String deleteById(@PathVariable Long postId, Model model) {
        serviceImplementation.delete(postId);
        return "redirect:/";
    }

}
