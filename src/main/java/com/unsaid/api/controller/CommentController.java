package com.unsaid.api.controller;

import com.unsaid.api.entity.Comment;
import com.unsaid.api.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    public static class CreateCommentRequest {
        public String content;
        public String authorHandle;
    }

    // add comment
    @PostMapping("/posts/{postId}/comments")
    public Comment addComment(@PathVariable Long postId,
                              @RequestBody CreateCommentRequest req) {

        if (req == null || req.content == null || req.content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        if (req.authorHandle == null || req.authorHandle.trim().isEmpty()) {
            throw new IllegalArgumentException("authorHandle is required");
        }

        return commentService.addComment(
                postId,
                req.content.trim(),
                req.authorHandle.trim()
        );
    }

    // get comments for a post
    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }
}
