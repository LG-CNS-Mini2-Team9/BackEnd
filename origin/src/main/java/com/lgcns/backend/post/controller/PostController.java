package com.lgcns.backend.post.controller;

import com.lgcns.backend.csquestion.domain.CSQuestion;
import com.lgcns.backend.csquestion.repository.CSQuestionRepository;
import com.lgcns.backend.global.response.CustomResponse;
import com.lgcns.backend.post.service.PostService;
import com.lgcns.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.lgcns.backend.post.dto.PostRequest.*;
import static com.lgcns.backend.post.dto.PostResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comm")
public class PostController {

    private final PostService postService;
    private final CSQuestionRepository cSQuestionRepository;

    @GetMapping
    public ResponseEntity<CustomResponse<PostListResponse>> getPostList(
            @RequestParam(required = false) String category,
            Pageable pageable
    ){
        if (category != null) {
            return ResponseEntity.ok(CustomResponse.ok(postService.getPostListByCategory(category, pageable)));
        } else {
            return ResponseEntity.ok(CustomResponse.ok(postService.getPostList(pageable)));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CustomResponse<?>> getPostDetail(
            @PathVariable Long postId
    ){
        return ResponseEntity.ok(CustomResponse.ok(postService.getPostDetail(postId)));
    }

    @PostMapping
    public ResponseEntity<CustomResponse<?>> createPost(
            @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        CSQuestion csQuestion = Optional.ofNullable(request.getQuestionId())
                .flatMap(cSQuestionRepository::findById)
                .orElse(null);

        return ResponseEntity.ok(CustomResponse.ok(postService.createPost(request, userDetails)));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<CustomResponse<?>> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(CustomResponse.ok(postService.updatePost(postId, request, userDetails)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<CustomResponse<?>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        postService.deletePost(postId, userDetails);
        return ResponseEntity.ok(CustomResponse.ok(null));
    }


}
