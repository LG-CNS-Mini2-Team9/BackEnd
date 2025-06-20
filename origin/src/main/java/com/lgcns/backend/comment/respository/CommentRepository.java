package com.lgcns.backend.comment.respository;

import com.lgcns.backend.comment.entity.Comment;
import com.lgcns.backend.post.entity.Post;
import com.lgcns.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    void deleteByUser(User user);
}
