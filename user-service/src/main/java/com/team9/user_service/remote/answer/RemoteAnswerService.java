package com.team9.user_service.remote.answer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "answer-service", path = "/answers")
public interface RemoteAnswerService {
    @DeleteMapping("/user/{userId}")
    void deleteAnswersByUser(@PathVariable String userId);
}
