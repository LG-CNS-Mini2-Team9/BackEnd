package com.lgcns.backend.comment.controller;

import com.lgcns.backend.comment.service.CommentService;
import com.lgcns.backend.global.response.CustomResponse;
import com.lgcns.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.lgcns.backend.comment.dto.CommentRequest.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comm")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<CustomResponse<?>> getCommentList(@PathVariable Long postId) {
        return ResponseEntity.ok(CustomResponse.ok(commentService.getCommentList(postId)));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CustomResponse<?>> addComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(CustomResponse.ok(commentService.addComment(postId, userDetails, request)));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CustomResponse<?>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.ok(CustomResponse.ok(commentService.updateComment(commentId, userDetails, request)));

    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CustomResponse<?>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails){

        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.ok(CustomResponse.ok(null));

    }

}
