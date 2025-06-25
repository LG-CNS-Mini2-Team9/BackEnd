package com.team9.user_service.controller.internal;

import com.team9.common.dto.ApiResponseDto;
import com.team9.user_service.domain.User;
import com.team9.user_service.dto.response.UserAnswerDto;
import com.team9.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/user")
public class InternalUserController {
    private final UserRepository userRepository;
    @GetMapping(value = "/id/{userId}")
    UserAnswerDto getUserById(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        UserAnswerDto userAnswerDto = new UserAnswerDto();
        userAnswerDto.setId(user.getId());
        userAnswerDto.setNickname(user.getNickname());

        return userAnswerDto;
    }



}
