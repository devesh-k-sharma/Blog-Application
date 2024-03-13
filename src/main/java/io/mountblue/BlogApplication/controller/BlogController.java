package io.mountblue.BlogApplication.controller;

import io.mountblue.BlogApplication.dao.ServiceImplementation;
import io.mountblue.BlogApplication.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private ServiceImplementation serviceImplementation;

    public BlogController(ServiceImplementation serviceImplementation){
        this.serviceImplementation = serviceImplementation;
    }

    @GetMapping("/page")
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
    public String saveForm(@ModelAttribute("form") Post post, @RequestParam String action) {
        if("Publish".equals(action)){
            return "redirect:/publish";
        }
        else if ("Save".equals(action)) {
            return "redirect:/save";
        }
        else {
            return "error";
        }
    }

    @PostMapping("/publish")
    public String publishBlog(@ModelAttribute("form") Post post) {
        serviceImplementation.saveWithIsPublishedTrue(post);
        return "redirect:/blog-application-front-page";
    }

    @PostMapping("/save")
    public String saveBlog(@ModelAttribute("form") Post post) {
        serviceImplementation.saveWithIsPublishedFalse(post);
        System.out.println(post.getCreatedAt());
        return "redirect:/blog-application-front-page";
    }
}
