package com.lgcns.backend.post.service;

import com.lgcns.backend.csquestion.domain.CSQuestion;
import com.lgcns.backend.csquestion.repository.CSQuestionRepository;
import com.lgcns.backend.global.code.PostErrorCode;
import com.lgcns.backend.global.domain.Category;
import com.lgcns.backend.global.exception.CustomException;
import com.lgcns.backend.post.entity.Post;
import com.lgcns.backend.post.respository.PostRepository;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.lgcns.backend.post.dto.PostRequest.*;
import static com.lgcns.backend.post.dto.PostResponse.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CSQuestionRepository cSQuestionRepository;

    //사용자, 게시글 작성자 확인용
    protected void validateWriter(Long writerId, Long currentUserId,String message) {
        if (!Objects.equals(writerId, currentUserId)) {
            throw new CustomException(PostErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(PostErrorCode.UNAUTHORIZED_ACCESS));
    }

    //게시글 목록 조회
    public PostListResponse getPostList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return PostListResponse.from(posts);
    }

    //카테고리별 목록 조회
    public PostListResponse getPostListByCategory(String category, Pageable pageable) {
        Category enumCategory = Category.valueOf(category.toUpperCase());
        Page<Post> posts = postRepository.findByCategory(enumCategory, pageable);
        return PostListResponse.from(posts);
    }

    //게시글 상세 조회
    public PostDetailResponse getPostDetail(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
        return PostDetailResponse.from(post);
    }

    //게시글 생성
    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request, UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);

        CSQuestion csQuestion = null;
        if (request.getQuestionId() != null) {
            csQuestion = cSQuestionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new CustomException(PostErrorCode.QUESTION_NOT_FOUND));
        }

        Category category;
        try {
            category = Category.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(PostErrorCode.INVALID_CATEGORY);
        }

        Post post = Post.builder()
                .user(user)
                .csQuestion(csQuestion)
                .content(request.getContent())
                .title(request.getTitle())
                .category(category)
                .build();

        postRepository.save(post);
        return PostCreateResponse.from(post);
    }

    //게시글 수정
    @Transactional
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, UserDetails userDetails){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
        User user = getUserFromDetails(userDetails);
        validateWriter(post.getUser().getId(), user.getId(), "작성자만 수정할 수 있습니다.");
        CSQuestion csQuestion = cSQuestionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new CustomException(PostErrorCode.QUESTION_NOT_FOUND));

        Category category;
        try {
            category = Category.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(PostErrorCode.INVALID_CATEGORY);
        }

        post.updatePost(category, request.getTitle(), request.getContent(), csQuestion);
        return PostUpdateResponse.from(post);
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long postId, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

        User user = getUserFromDetails(userDetails);
        validateWriter(post.getUser().getId(), user.getId(), "작성자만 삭제할 수 있습니다.");

        postRepository.delete(post);
    }
}
