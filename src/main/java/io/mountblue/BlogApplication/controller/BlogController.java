package io.mountblue.BlogApplication.controller;

import io.mountblue.BlogApplication.dao.ServiceImplementation;
import io.mountblue.BlogApplication.entity.Comment;
import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/drafts")
    public String showDraftPage(Model model) {
        List<Post> posts = serviceImplementation.showAllPublishedBlogs(false);
        model.addAttribute("posts", posts);
        return "all-drafts";
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
        } else if ("Save".equals(action)) {
            post.setIs_published(false);
        }
        String paragraph = post.getContent();
        String[] words = paragraph.split("\\s+");
        int maxLength = Math.min(words.length, 30);
        String excerpt = String.join(" ", Arrays.copyOf(words, maxLength));
        post.setExcerpt(excerpt);

        String[] tagNames = tagsString.split(",");
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
        serviceImplementation.save(post);

        return "redirect:/";
    }

    @GetMapping("/post{postId}")
    public String viewBlogPost(@PathVariable Long postId, Model model) {
        Post post = serviceImplementation.getPostById(postId);
        List<Comment> comments = post.getComments();
        System.out.println("Number of comments for post " + postId + ": " + comments.size());
        String theComment = null;
        model.addAttribute("theComment", theComment);
        model.addAttribute("comments", comments);
        model.addAttribute("post", post);
        return "blogpost";
    }

    @GetMapping("/update{postId}")
    public String updateById(@PathVariable Long postId, Model model) {
        Post post = serviceImplementation.getPostById(postId);
        if (post != null) {
            model.addAttribute("post", post);
            List<Tag> tags = post.getTags();
            StringBuilder tagListBuilder = new StringBuilder();
            for (Tag tag : tags) {
                tagListBuilder.append(tag.getName()).append(","); // Add a comma after each tag name
            }
            String tagList = tagListBuilder.toString();
            model.addAttribute("mytags", tagList);
            return "edit-post";
        } else {
            return "error";
        }
    }

    @PostMapping("/update{postId}")
    public String updateForm(@PathVariable Long postId, @ModelAttribute("post") Post post, @RequestParam("mytags") String tagsString) {
        Post posts = serviceImplementation.getPostById(postId);
        posts.setTitle(post.getTitle());
        posts.setExcerpt(post.getExcerpt());
        posts.setContent(post.getContent());

        String[] tagNames = tagsString.split(",");
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
        posts.setTags(tags);
        serviceImplementation.publish(postId, true);
        serviceImplementation.save(posts);
        return "redirect:/";
    }

    @GetMapping("/delete{postId}")
    public String deleteById(@PathVariable Long postId, Model model) {
        serviceImplementation.delete(postId);
        return "redirect:/";
    }

    @PostMapping("/addcomment{postId}")
    public String addComment(@PathVariable Long postId,@RequestParam("newComment") String newCommentText) {
        Post post = serviceImplementation.getPostById(postId);
        Comment newComment = new Comment();
        newComment.setComment(newCommentText);
        newComment.setPost(post);
        post.getComments().add(newComment);
        serviceImplementation.save(post);
        return "redirect:/post" + postId;
    }

    @GetMapping("/updatecomment{postId}/{commentId}")
    public String updateComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId, Model model) {
        Post post = serviceImplementation.getPostById(postId);
        List<Comment> comments = post.getComments();
        Comment comment = serviceImplementation.getComment(postId, commentId);
        if(comment != null) {
            String theComment = comment.getComment();
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            model.addAttribute("theComment", theComment);
            model.addAttribute("comment", comment);
            serviceImplementation.deleteComment(commentId,postId);
            return "blogpost";
        }
        else {
            return "error";
        }
    }

    @GetMapping("/deletecomment{postId}/{commentId}")
    public String deleteComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId) {
        serviceImplementation.deleteComment(commentId, postId);

        return "redirect:/post" + postId;
    }

}
