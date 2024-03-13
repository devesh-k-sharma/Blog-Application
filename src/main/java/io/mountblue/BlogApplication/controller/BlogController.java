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
import java.util.List;

@Controller
public class BlogController {

    private ServiceImplementation serviceImplementation;

    public BlogController(ServiceImplementation serviceImplementation){
        this.serviceImplementation = serviceImplementation;
    }

    @GetMapping("/")
    public String showPage(Model model) {
        List<Post> posts = serviceImplementation.showPage();
        model.addAttribute("posts", posts);
        return "blog-application-front-page";
    }

    @GetMapping("/newpost")
    public String newPost(Model model) {
        model.addAttribute("form", new Post());
        return "new-post";
    }

    @PostMapping("/action")
    public String saveForm(@ModelAttribute("form") Post post, @RequestParam String action, @RequestParam("tagList") String tagsString) {
        if ("Publish".equals(action)) {
            Post existingPost = serviceImplementation.findPostByTitleAndContent(post.getTitle(), post.getContent());
            if (existingPost != null) {
                serviceImplementation.publish(existingPost.getId(), true);
                return "blog-application-front-page";
            }
            // If the post doesn't exist, proceed to publish the submitted post
            post.setIs_published(true);
            post.setPublished_at(LocalDateTime.now());
        }
        else if ("Save".equals(action)) {
            post.setIs_published(false);
        }
        String[] tagNames = tagsString.split(",");
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tags.add(tag);
        }
        post.setTags(tags);
        serviceImplementation.save(post);
        return "blog-application-front-page";
    }

}
