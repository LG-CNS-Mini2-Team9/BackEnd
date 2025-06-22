package com.team9.answer_service.remote.user;


import com.team9.answer_service.remote.user.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "backend-user", path="/backend/user/v1")
public interface RemoteUserService {
    @GetMapping(value = "/{userId}")
    UserDto.CSAnswerUserDto getUserById(@PathVariable("userId") Long userId);

    @GetMapping(value="/{email}")
    Long getUserIdByEmail(@PathVariable("email") String email);
}
