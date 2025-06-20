package com.lgcns.backend.post.entity;

import com.lgcns.backend.comment.entity.Comment;
import com.lgcns.backend.csquestion.domain.CSQuestion;
import com.lgcns.backend.global.domain.Category;
import com.lgcns.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = true)
    private CSQuestion csQuestion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public List<Comment> getComments() {
        return comments;
    }

    public void updatePost(Category category, String title, String content, CSQuestion csQuestion) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.csQuestion = csQuestion;
    }

    //자동 주입
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
