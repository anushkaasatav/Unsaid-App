package com.unsaid.api.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String authorHandle;

    @Column(nullable = false)
    private Instant createdAt;

    // Likes stored in DB
    @Column
    private Integer likeCount = 0;

    // Comments count (calculated, not stored)
    @Transient
    private Long commentCount = 0L;

    public Post() {}

    public Post(String content, String authorHandle) {
        this.content = content;
        this.authorHandle = authorHandle;
        this.createdAt = Instant.now();
        this.likeCount = 0;
        this.commentCount = 0L;
    }

    public void incrementLike() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount++;
    }

    // getters
    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorHandle() { return authorHandle; }
    public Instant getCreatedAt() { return createdAt; }

    public int getLikeCount() {
        return (likeCount == null) ? 0 : likeCount;
    }

    public Long getCommentCount() {
        return (commentCount == null) ? 0L : commentCount;
    }

    // setter used by PostService
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}
