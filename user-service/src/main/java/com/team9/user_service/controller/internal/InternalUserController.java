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

    @GetMapping(value = "/email/{email}")
    Long getUserIdByEmail(@PathVariable("email") String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    @PostMapping("/validate")
    public ApiResponseDto<String> validate(@RequestBody ValidateUserDto validateUserDto) {
        // 예시: 이메일과 비밀번호로 사용자 존재 여부 확인
        Optional<User> userOpt = userRepository.findByEmail(validateUserDto.getEmail());

        if (userOpt.isEmpty()) {
            return ApiResponseDto.createError("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }

        User user = userOpt.get();

        // 비밀번호 체크 (예: plain text 비교 또는 암호화 비교)
        if (!user.getPassword().equals(validateUserDto.getPassword())) {
            return ApiResponseDto.createError("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");
        }

        return ApiResponseDto.createOk("OK");
    }

}
