package com.team9.question_service.controller.internal;

import com.team9.question_service.global.response.CustomResponse;
import com.team9.question_service.dto.QuestionResponse;
import com.team9.question_service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/questions")
public class InternalQuestionController {
    private final QuestionService questionService;

    @GetMapping("/{questionId}")
    public ResponseEntity<CustomResponse<QuestionResponse>> getQuestionById(
            @PathVariable("questionId") Long questionId) {
        QuestionResponse question = questionService.getQuestionDetail(questionId, null);
        return ResponseEntity.ok(CustomResponse.ok(question));
    }
}
