package io.mountblue.BlogApplication.controller;

import io.mountblue.BlogApplication.dao.ServiceImplementation;
import io.mountblue.BlogApplication.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "index.html";
    }
}
