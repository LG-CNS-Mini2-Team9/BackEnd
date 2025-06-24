package com.team9.api_gateway.auth.api.open;

import com.team9.api_gateway.auth.dto.LoginDto;
import com.team9.api_gateway.auth.dto.RefreshDto;
import com.team9.api_gateway.auth.jwt.dto.TokenDto;
import com.team9.api_gateway.auth.service.AuthService;
import com.team9.api_gateway.common.dto.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/test")
    public ApiResponseDto<String> test() {
        log.info("hello world");
        return ApiResponseDto.createOk("hello world");
    }


    @PostMapping("/login")
    public ApiResponseDto<TokenDto.AccessRefreshToken> login(@RequestBody @Valid LoginDto loginDto) {
        TokenDto.AccessRefreshToken token = authService.login(loginDto);

        return ApiResponseDto.createOk(token);
    }

    @PostMapping("/refresh")
    public ApiResponseDto<TokenDto.AccessToken> refresh(@RequestBody @Valid RefreshDto refreshDto) {
        TokenDto.AccessToken token = authService.refresh(refreshDto);

        return ApiResponseDto.createOk(token);
    }
}
