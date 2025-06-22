package com.team9.answer_service.remote.csquestion.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "backend-csquestion", path="/backend/csquestion/v1")
public interface RemoteCSQuestionService {
    @GetMapping(value="/{questionId}")
    CSQuestionDto.Response getQuestionById(@PathVariable("questionId") Long id);
}
