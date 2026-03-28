package com.unsaid.api.repository;

import com.unsaid.api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // fetch comments for a post (sorted)
    List<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId);

    // count comments for a post
    long countByPost_Id(Long postId);
}
