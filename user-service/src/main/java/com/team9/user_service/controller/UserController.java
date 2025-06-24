package com.lgcns.backend.user.controller;

import com.lgcns.backend.global.code.GeneralErrorCode;
import com.lgcns.backend.global.code.GeneralSuccessCode;
import com.lgcns.backend.global.response.CustomResponse;
import com.lgcns.backend.security.util.JwtUtil;
import com.lgcns.backend.user.dto.request.LoginRequestDto;
import com.lgcns.backend.user.dto.request.SignUpRequestDto;
import com.lgcns.backend.user.dto.request.UpdateUserRequestDto;
import com.lgcns.backend.user.service.S3Service;
import com.lgcns.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    S3Service s3Service;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<CustomResponse<String>> signUp(@ModelAttribute SignUpRequestDto dto) {

        try {
            MultipartFile image = dto.getImage();
            String imageUrl = s3Service.upload(image);


            userService.signUp(dto, imageUrl);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(CustomResponse.created("회원가입 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(GeneralErrorCode._EMAIL_USED.getHttpStatus())
                    .body(CustomResponse.fail(GeneralErrorCode._EMAIL_USED, "이미 존재하는 이메일입니다."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<CustomResponse<String>> login(@RequestBody LoginRequestDto dto) {
        CustomResponse<String> response = userService.login(dto);
        
        // 리턴이 실패인 경우
        if (!response.isSuccess()) {
            return ResponseEntity.status(GeneralErrorCode._UNAUTHORIZED.getHttpStatus())
                    .body(response);
        }
        
        // 성공인 경우
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/delete")
    public ResponseEntity<CustomResponse<String>> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {

        userService.deleteUser(userDetails.getUsername()); // DB의 회원정보 삭제
        redisTemplate.delete(userDetails.getUsername()); // redis의 refreshToken 삭제

        return ResponseEntity
                .status(GeneralSuccessCode._OK.getHttpStatus())
                .body(CustomResponse.success(GeneralSuccessCode._OK, "회원 탈퇴 완료"));
    }

    @PostMapping("/user/update")
    public ResponseEntity<CustomResponse<String>> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                           @ModelAttribute UpdateUserRequestDto dto) throws IOException {

//        MultipartFile image = dto.getImage();
//        String imageUrl = s3Service.upload(image);

        CustomResponse<String> response = userService.updateUser(userDetails.getUsername(), dto);

        // 모든 오류 케이스에 대해 오류 메시지 달리하여 출력
        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) 
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<CustomResponse<String>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername(); // 사용자 이메일 토큰으로 추출
        redisTemplate.delete(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CustomResponse.ok("로그아웃 완료"));
    }
}