package com.team9.user_service.domain;

import com.team9.user_service.global.domain.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    // @Column(nullable = false)
    private String profileImage;

    private String provider;

    private String role;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_category",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<Category> interests;
}
