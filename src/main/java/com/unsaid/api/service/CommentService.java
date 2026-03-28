package com.unsaid.api.service;

import com.unsaid.api.entity.Comment;
import com.unsaid.api.entity.Post;
import com.unsaid.api.repository.CommentRepository;
import com.unsaid.api.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    // add a new comment to a post
    public Comment addComment(Long postId, String content, String authorHandle) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment(post, content, authorHandle);
        return commentRepository.save(comment);
    }

    // get all comments for a post (sorted by createdAt ASC)
    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPost_IdOrderByCreatedAtAsc(postId);
    }
}
