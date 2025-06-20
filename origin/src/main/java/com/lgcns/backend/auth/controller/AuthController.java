package com.lgcns.backend.auth.controller;


import com.lgcns.backend.auth.service.AuthService;
import com.lgcns.backend.global.code.GeneralErrorCode;
import com.lgcns.backend.global.response.CustomResponse;
import com.lgcns.backend.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<CustomResponse<String>> reissue(
            @RequestHeader("Authorization") String refreshTokenHeader) {

        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.fail(GeneralErrorCode._UNAUTHORIZED, "Refresh Token이 없습니다."));
        }

        String refreshToken = refreshTokenHeader.substring(7);

        try {
            CustomResponse<String> response = authService.reissueToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (ExpiredJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.fail(GeneralErrorCode._TOKEN_EXPIRED, "Refresh Token이 만료 되었습니다."));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.fail(GeneralErrorCode._UNAUTHORIZED, "Refresh Token이 유효하지 않습니다."));
        }
    }
}
