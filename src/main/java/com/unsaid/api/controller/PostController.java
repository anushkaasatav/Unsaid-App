package com.unsaid.api.controller;

import com.unsaid.api.entity.Post;
import com.unsaid.api.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public static class CreatePostRequest {
        public String content;
        public String authorHandle;
    }

    @PostMapping("/posts")
    public Post createPost(@RequestBody CreatePostRequest req) {
        if (req == null || req.content == null || req.content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (req.authorHandle == null || req.authorHandle.trim().isEmpty()) {
            throw new IllegalArgumentException("authorHandle is required");
        }
        return postService.createPost(req.content.trim(), req.authorHandle.trim());
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        // IMPORTANT: this must call service so commentCount is computed
        return postService.getAllPosts();
    }

    @PostMapping("/posts/{id}/like")
    public Post likePost(@PathVariable Long id) {
        // Return post with updated likeCount + correct commentCount
        return postService.likePost(id);
    }
}
