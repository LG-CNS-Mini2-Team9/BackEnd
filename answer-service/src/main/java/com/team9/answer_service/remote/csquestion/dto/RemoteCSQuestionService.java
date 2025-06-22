package com.team9.answer_service.remote.csquestion.dto;

@FeignClient(name = "backend-csquestion", path="/backend/csquestion/v1")
public interface RemoteCSQuestionService {
}
