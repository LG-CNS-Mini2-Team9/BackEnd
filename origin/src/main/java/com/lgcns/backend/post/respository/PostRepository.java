package com.lgcns.backend.post.respository;

import com.lgcns.backend.post.entity.Post;
import com.lgcns.backend.global.domain.Category;
import com.lgcns.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByCategory(Category category, Pageable pageable);
    void deleteByUser(User user);
}
