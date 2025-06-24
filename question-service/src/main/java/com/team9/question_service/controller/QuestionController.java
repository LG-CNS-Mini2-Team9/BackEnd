package com.team9.question_service.controller;

import com.team9.common.response.CustomResponse;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<CustomResponse<Page<QuestionResponse>>> getQuestionList(
            @RequestParam(required = false) String category,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Page<QuestionResponse> list = questionService.getQuestionList(category, pageable, userId);
        return ResponseEntity.ok(CustomResponse.ok(list));
    }

    // X-User-Id 헤더를 받도록 수정
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<QuestionResponse>> getQuestionDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) { // userId 파라미터 추가
        QuestionResponse question = questionService.getQuestionDetail(id, userId); // userId 전달
        return ResponseEntity.ok(CustomResponse.ok(question));
    }

    @GetMapping("/today")
    public ResponseEntity<CustomResponse<QuestionResponse>> getTodayQuestion(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        QuestionResponse question = questionService.getTodayQuestion(userId);
        return ResponseEntity.ok(CustomResponse.ok(question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Void>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(CustomResponse.ok(null));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<CustomResponse<Page<QuestionResponse>>> getQuestionsByCategory(
            @PathVariable String category,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Page<QuestionResponse> list = questionService.getQuestionList(category, pageable, userId);
        return ResponseEntity.ok(CustomResponse.ok(list));
    }
}