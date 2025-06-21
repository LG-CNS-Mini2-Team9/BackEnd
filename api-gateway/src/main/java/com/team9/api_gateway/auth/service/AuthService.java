package com.team9.api_gateway.auth.service;

import com.team9.api_gateway.auth.dto.LoginDto;
import com.team9.api_gateway.auth.dto.RefreshDto;
import com.team9.api_gateway.auth.jwt.TokenGenerator;
import com.team9.api_gateway.auth.jwt.TokenValidator;
import com.team9.api_gateway.auth.jwt.dto.TokenDto;
import com.team9.api_gateway.auth.remote.user.RemoteUserService;
import com.team9.api_gateway.auth.remote.user.dto.ValidateUserDto;
import com.team9.api_gateway.common.exception.BadParameter;
import com.team9.api_gateway.common.exception.NotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenGenerator jwtTokenGenerator;
    private final TokenValidator jwtTokenValidator;
    private final RemoteUserService remoteUserService;

    public TokenDto.AccessRefreshToken login(LoginDto loginDto) {

        ValidateUserDto validateUserDto = ValidateUserDto.of(loginDto);
        String code = remoteUserService.validate(validateUserDto).getCode();

        if (!"OK".equals(code)) {
            throw new BadParameter("사용자 아이디 또는 비밀번호를 확인하세요.");
        }

        return jwtTokenGenerator.generateAccessRefreshToken(loginDto.getUserId(), "WEB");
    }

    public TokenDto.AccessToken refresh(RefreshDto refreshDto) {
        String userId = jwtTokenValidator.validateJwtToken(refreshDto.getToken());

        if (userId == null) {
            throw new BadParameter("토큰이 유효하지 않습니다.");
        }

        String exists = remoteUserService.exists(userId).getCode();
        if (!"OK".equals(exists)) {
            throw new NotFound("사용자가 존재하지 않습니다.");
        }

        return jwtTokenGenerator.generateAccessToken(userId, "WEB");
    }

}
