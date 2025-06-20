package com.lgcns.backend.csquestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.lgcns.backend.csquestion.dto.CSQuestionResponse.AIQuestionResponse;
import com.lgcns.backend.csquestion.service.AIQuestionService;
import com.lgcns.backend.global.response.CustomResponse;

import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class AIQuestionController {
    
    @Autowired
    private AIQuestionService aiQuestionService;

    @PostMapping("/api/questions")
    public ResponseEntity<CustomResponse<AIQuestionResponse>> generateAIQuestion() {
        AIQuestionResponse response = aiQuestionService.createAIQuestion();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.created(response)); 
    }
}
