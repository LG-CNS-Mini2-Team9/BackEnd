package com.team9.answer_service.remote.csquestion;

import com.team9.answer_service.remote.csquestion.dto.CSQuestionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "question-service", path="/internal/questions")
public interface RemoteCSQuestionService {
    @GetMapping(value="/{questionId}")
    CSQuestionDto.Response getQuestionById(@PathVariable("questionId") Long questionId);
}
