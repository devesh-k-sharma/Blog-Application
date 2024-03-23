package io.mountblue.BlogApplication.controller;

import io.mountblue.BlogApplication.dao.ServiceImplementation;
import io.mountblue.BlogApplication.entity.Comment;
import io.mountblue.BlogApplication.entity.Post;
import io.mountblue.BlogApplication.entity.Tag;
import io.mountblue.BlogApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
public class BlogController {

    private ServiceImplementation serviceImplementation;

    public BlogController(ServiceImplementation serviceImplementation){
        this.serviceImplementation = serviceImplementation;
    }

    @GetMapping("/")
    public String showPage(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = serviceImplementation.showAllPublishedBlogsPaged(true, pageable);

        //List<Post> posts = postList.getContent();
        // List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
        List<Tag> tags = new ArrayList<>();
        //List<String> author = new ArrayList<>();
        String search = "search.....";
        List<String> tagList = new ArrayList<>();
        for(Post post: posts) {
            tags = post.getTags();
            User users = post.getAuthor();
            //author.add(users.getName());
            for(Tag tag: tags) {
                if(!tagList.contains(tag.getName())){
                    tagList.add(tag.getName());
                }
            }
        }
        List<String> author = new ArrayList<>();
//        LocalDate oldestDate = serviceImplementation.oldestDate();
//        LocalDate newestDate = serviceImplementation.newestDate();
        model.addAttribute("tags" , tagList);
        model.addAttribute("author", author);
        model.addAttribute("searchbar", search);
        model.addAttribute("posts", posts);
//        model.addAttribute("startDate", oldestDate);
//        model.addAttribute("endDate", newestDate);
        return "all-blogs";
    }

//    @GetMapping("/")
//    public String showPage(@RequestParam(defaultValue = "0") String pageStr,
//                           @RequestParam(defaultValue = "10") String sizeStr,
//                           Model model) {
//        int page;
//        int size;
//        try {
//            page = Integer.parseInt(pageStr);
//            size = Integer.parseInt(sizeStr);
//        } catch (NumberFormatException e) {
//            page = 0;
//            size = 10;
//        }
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Post> posts = serviceImplementation.showAllPublishedBlogsPaged(true, pageable);
//
//        //List<Post> posts = postList.getContent();
//        // List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
//        List<Tag> tags = new ArrayList<>();
//        //List<String> author = new ArrayList<>();
//        String search = "search.....";
//        List<String> tagList = new ArrayList<>();
//        for(Post post: posts) {
//            tags = post.getTags();
//            User users = post.getAuthor();
//            //author.add(users.getName());
//            for(Tag tag: tags) {
//                if(!tagList.contains(tag.getName())){
//                    tagList.add(tag.getName());
//                }
//            }
//        }
//        List<String> author = new ArrayList<>();
////        LocalDate oldestDate = serviceImplementation.oldestDate();
////        LocalDate newestDate = serviceImplementation.newestDate();
//        model.addAttribute("tags" , tagList);
//        model.addAttribute("author", author);
//        model.addAttribute("searchbar", search);
//        model.addAttribute("posts", posts);
////        model.addAttribute("startDate", oldestDate);
////        model.addAttribute("endDate", newestDate);
//        return "all-blogs";
//    }

    static String search = "";
    @GetMapping("/features")
    public String showFeaturePage(@RequestParam(value = "sortType", required = false) String sortType,
                                  @RequestParam(value = "searchFor", required = false) String searchFor,
                                  @RequestParam(value = "filter-author", required = false) List<String> selectedAuthors,
                                  @RequestParam(value = "filter-tags", required = false) List<String> selectedTags,
                                  @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        List<Post> posts = new ArrayList<>();
        LocalDateTime startDateTime = startDate != null ? LocalDateTime.of(startDate, LocalTime.MIN) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;
        posts = serviceImplementation.features(searchFor, sortType, selectedAuthors, selectedTags, startDateTime, endDateTime);

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), posts.size());
        Page<Post> paginatedPosts = new PageImpl<>(posts.subList(start, end), pageable, posts.size());

//        if(searchFor.equals(search)) {
//            posts = serviceImplementation.features(searchFor, sortType, selectedAuthors, selectedTags);
//        }
//        else {
//            selectedAuthors = new ArrayList<>();
//            selectedTags = new ArrayList<>();
//            posts = serviceImplementation.features(searchFor, sortType, selectedAuthors, selectedTags);
//            search = searchFor;
//        }
        List<Tag> tags = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        for(Post post: posts) {
            tags = post.getTags();
            User users = post.getAuthor();
            //author.add(users.getName());
            for(Tag tag: tags) {
                if(!tagList.contains(tag.getName())){
                    tagList.add(tag.getName());
                }
            }
        }
        List<String> author = new ArrayList<>();
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("tags" , tagList);
        model.addAttribute("author", author);
        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("selectedAuthors", selectedAuthors);
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("searchFor", searchFor);

        return "all-blogs";
    }




//    @GetMapping("/features")
//    public String showFeaturesPage(@RequestParam(value = "sortType", required = false) String sortType,
//                                   @RequestParam(value = "searchFor", required = false) String searchFor,
//                                   @RequestParam(value = "filter-author", required = false) List<String> selectedAuthors,
//                                   @RequestParam(value = "filter-tags", required = false) List<String> selectedTags,
//                                   Model model) {
//        List<Post> posts = new ArrayList<>();
//        List<String> features = new ArrayList<>();
//        features.addAll(selectedAuthors);
//        features.addAll(selectedTags);
//        System.out.println(features);
////        if(sortType != null && searchFor.isEmpty() && features == null) {
////            posts.addAll(serviceImplementation.showAllSortBy(sortType));
////        } else if (sortType == null && searchFor != null && features == null) {
////            posts.addAll(serviceImplementation.showAllSearchBy(searchFor));
////            selectedAuthors.clear();
////            selectedTags.clear();
////        } else if (sortType != null && !searchFor.isEmpty() && features == null) {
////            posts.addAll(serviceImplementation.multipleFeatures(searchFor, sortType, selectedAuthors, selectedTags));
////        } else if (sortType == null && searchFor.isEmpty() && features != null) {
////            posts.addAll(serviceImplementation.filterBy(selectedAuthors, selectedTags));
////        }
////        else if (sortType == null && !searchFor.isEmpty() && features != null) {
////
////        } else if (sortType != null && !searchFor.isEmpty() && features != null) {
////
////        }
////        else {
////            return "redirect:/";
////        }
//
////        List<Post> posts = serviceImplementation.showAllPublishedBlogs(true);
//        List<Tag> tags = new ArrayList<>();
//        //List<String> author = new ArrayList<>();
//        //String search = "search.....";
//        List<String> tagList = new ArrayList<>();
//        for(Post post: posts) {
//            tags = post.getTags();
//            User users = post.getAuthor();
//            //author.add(users.getName());
//            for(Tag tag: tags) {
//                if(!tagList.contains(tag.getName())){
//                    tagList.add(tag.getName());
//                }
//            }
//        }
//        List<String> author = new ArrayList<>();
//        model.addAttribute("tags" , tagList);
//        model.addAttribute("author", author);
//
//        model.addAttribute("selectedAuthors", selectedAuthors);
//        model.addAttribute("selectedTags", selectedTags);
//        model.addAttribute("searchFor", searchFor);
//        model.addAttribute("posts", posts);
//
//        return "all-blogs";
//    }


//    @GetMapping("/sort/{sortType}")
//    public String sort(@PathVariable String sortType, Model model) {
//        List<Post> posts =serviceImplementation.showAllSortBy(sortType);
//        model.addAttribute("posts", posts);
//        return "all-blogs";
//    }

//    @GetMapping("/sort")
//    public String sortBlogs(@RequestParam(value = "sortType") String sortType,
//                            @RequestParam(value = "searchFor", required = false) String searchFor,
//                            Model model) {
//        List<Post> posts;
//
//        if (searchFor != null && !searchFor.isEmpty()) {
//            posts = serviceImplementation.sortSearchResults(searchFor, sortType);
//        } else {
//            posts = serviceImplementation.showAllSortBy(sortType);
//        }
//        model.addAttribute("posts", posts);
//        return "all-blogs";
//    }
//
//    @GetMapping("/search")
//    public String search(@RequestParam("searchFor") String searchFor, Model model) {
//        List<Post> posts= serviceImplementation.showAllSearchBy(searchFor);
//        model.addAttribute("posts", posts);
//        return "all-blogs";
//    }

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
        List<Tag> tags = post.getTags();
        StringBuilder tagListBuilder = new StringBuilder();
        for (Tag tag : tags) {
            tagListBuilder.append(tag.getName()).append(" "); // Add a comma after each tag name
        }
        String tagList = tagListBuilder.toString();
        model.addAttribute("mytags", tagList);
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
