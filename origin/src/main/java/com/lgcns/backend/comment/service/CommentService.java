package com.lgcns.backend.comment.service;

import com.lgcns.backend.comment.entity.Comment;
import com.lgcns.backend.comment.respository.CommentRepository;
import com.lgcns.backend.global.code.CommentErrorCode;
import com.lgcns.backend.global.code.PostErrorCode;
import com.lgcns.backend.global.exception.CustomException;
import com.lgcns.backend.post.entity.Post;
import com.lgcns.backend.post.respository.PostRepository;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.lgcns.backend.comment.dto.CommentRequest.*;
import static com.lgcns.backend.comment.dto.CommentResponse.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    protected void validateWriter(Long writerId, Long currentUserId, com.lgcns.backend.global.code.CommentErrorCode errorCode) {
        if (!Objects.equals(writerId, currentUserId)) {
            throw new CustomException(errorCode);
        }
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(PostErrorCode.UNAUTHORIZED_ACCESS));
    }

    //댓글 목록 조회
    public CommentListResponse getCommentList(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(com.lgcns.backend.global.code.CommentErrorCode.POST_NOT_FOUND));
        List<Comment> comments = commentRepository.findByPost(post);
        return CommentListResponse.from(comments);
    }

    //댓글 작성
    @Transactional
    public CommentCreateResponse addComment(Long postId, UserDetails userDetails, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.POST_NOT_FOUND));

        User user = getUserFromDetails(userDetails);

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return CommentCreateResponse.from(comment);
    }

    //댓글 수정
    @Transactional
    public CommentUpdateResponse updateComment(Long commentId, UserDetails userDetails, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

        User user = getUserFromDetails(userDetails);
        validateWriter(comment.getUser().getId(), user.getId(), CommentErrorCode.NO_UPDATE_PERMISSION);

        comment.updateComment(request.getContent());
        return CommentUpdateResponse.from(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, UserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

        User user = getUserFromDetails(userDetails);
        validateWriter(comment.getUser().getId(), user.getId(), CommentErrorCode.NO_DELETE_PERMISSION);

        commentRepository.delete(comment);
    }

}
