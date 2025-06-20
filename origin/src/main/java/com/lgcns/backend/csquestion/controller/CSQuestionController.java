package com.lgcns.backend.csquestion.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lgcns.backend.csquestion.dto.CSQuestionResponse;
import com.lgcns.backend.csquestion.service.CSQuestionService;
import com.lgcns.backend.global.code.GeneralSuccessCode;
import com.lgcns.backend.global.response.CustomResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/questions")
public class CSQuestionController {

    @Autowired
    CSQuestionService csQuestionService;

    @GetMapping
    public ResponseEntity<?> getCSQuestionList(@RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page, @AuthenticationPrincipal UserDetails userDetails) {
        Order order = Order.desc("id");
        Sort sort = Sort.by(order);
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<CSQuestionResponse> list = csQuestionService.getCSQuestionList(category, pageable, userDetails);
        return ResponseEntity.ok(CustomResponse.ok(list));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getCSQuestion(@PathVariable Long questionId) {
        CSQuestionResponse question = csQuestionService.getCSQuestion(questionId);

        return ResponseEntity.ok(CustomResponse.ok(question));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayCSQuestion() {
        CSQuestionResponse question = csQuestionService.getTodayCSQuestion();

        return ResponseEntity.ok(CustomResponse.ok(question));
    }

    @PostMapping("/{questionId}/delete")
    public ResponseEntity<?> deleteCSQuestion(@PathVariable Long questionId) {
        csQuestionService.deleteQuestion(questionId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(CustomResponse.success(GeneralSuccessCode._DELETED, null));
    }
}
