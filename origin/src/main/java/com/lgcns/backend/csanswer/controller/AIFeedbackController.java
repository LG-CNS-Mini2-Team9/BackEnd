package com.lgcns.backend.csanswer.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lgcns.backend.csanswer.dto.CSAnswerResponse;
import com.lgcns.backend.csanswer.dto.CSAnswerResponse.AIFeedbackResponse;
import com.lgcns.backend.csanswer.service.AIFeedbackService;
import com.lgcns.backend.global.response.CustomResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class AIFeedbackController {
    
    @Autowired
    private AIFeedbackService aiFeedbackService;
    
    @PostMapping("/api/answers/{answerId}")
    public ResponseEntity<CustomResponse<AIFeedbackResponse>> generateAIFeedback(
            @PathVariable Long answerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        CSAnswerResponse.AIFeedbackResponse response = aiFeedbackService.createAIFeedback(answerId, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.created(response));
    }
    
}
