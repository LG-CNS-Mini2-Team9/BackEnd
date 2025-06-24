package com.team9.answer_service.api.backend;

import com.team9.answer_service.global.domain.Category;
import com.team9.answer_service.service.CSAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/backend/answers/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BackendCSAnswerController {
    private final CSAnswerService csAnswerService;

    @GetMapping("/count")
    public Long countSolvedQuestion(@RequestParam(required=false) String categoryName, @AuthenticationPrincipal UserDetails userDetails){
        return csAnswerService.countSolvedQuestion(categoryName, userDetails);
    }
}
