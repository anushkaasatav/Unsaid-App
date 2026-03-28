package com.unsaid.api.service;

import com.unsaid.api.entity.Post;
import com.unsaid.api.repository.CommentRepository;
import com.unsaid.api.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Post createPost(String content, String authorHandle) {
        Post post = new Post(content, authorHandle);
        Post saved = postRepository.save(post);

        // make response consistent (commentCount should exist)
        saved.setCommentCount(0L);
        return saved;
    }

    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        posts.forEach(p -> p.setCommentCount(commentRepository.countByPost_Id(p.getId())));
        return posts;
    }

    public Post likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.incrementLike();
        Post saved = postRepository.save(post);

        // return like response with correct commentCount too
        saved.setCommentCount(commentRepository.countByPost_Id(saved.getId()));
        return saved;
    }
}
