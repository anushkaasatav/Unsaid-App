package com.unsaid.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 600)
    private String content;

    @Column(nullable = false)
    private String authorHandle;

    @Column(nullable = false)
    private Instant createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    public Comment() {}

    public Comment(Post post, String content, String authorHandle) {
        this.post = post;
        this.content = content;
        this.authorHandle = authorHandle;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorHandle() { return authorHandle; }
    public Instant getCreatedAt() { return createdAt; }

    // so API shows "postId": <id>
    public Long getPostId() {
        return (post == null) ? null : post.getId();
    }

    // edit support (optional)
    public void setContent(String content) {
        this.content = content;
    }

    // internal use (service/repo)
    public Post getPost() {
        return post;
    }
}
