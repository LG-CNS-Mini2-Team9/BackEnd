package com.team9.user_service.controller;

import com.team9.common.dto.ApiResponseDto;
import com.team9.user_service.domain.User;
import com.team9.user_service.dto.request.LoginRequestDto;
import com.team9.user_service.dto.request.SignUpRequestDto;
import com.team9.user_service.dto.request.UpdateUserProfileRequestDto;
import com.team9.user_service.dto.response.UserProfileResponseDto;
import com.team9.user_service.global.code.GeneralErrorCode;
import com.team9.user_service.global.code.GeneralSuccessCode;
import com.team9.user_service.global.response.CustomResponse;
import com.team9.user_service.repository.UserRepository;
import com.team9.user_service.service.S3Service;
import com.team9.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    S3Service s3Service;

    @Autowired
    UserRepository userRepository;

    // 회원가입
    @PostMapping("/api/users/signup")
    public ResponseEntity<CustomResponse<String>> signUp(@ModelAttribute SignUpRequestDto dto) {
        try {
            String imageUrl = s3Service.upload(dto.getImage());
            userService.signUp(dto, imageUrl);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(CustomResponse.created("회원가입 성공"));
        } catch (Exception e) {
            System.out.println("회원가입 실패: " + e.getMessage());
            e.printStackTrace();  // 상세 로그 출력
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "회원가입 중 에러 발생"));
        }
    }


    // 로그인 (api-gateway)
    @PostMapping("/backend/user/v1/validate")
    public ApiResponseDto<String> validate(@RequestBody LoginRequestDto dto) {
        boolean isValid = userService.validateCredentials(dto.getEmail(), dto.getPassword());

        if (isValid) {
            return ApiResponseDto.defaultOk();
        } else {
            return ApiResponseDto.createError("UNAUTHORIZED", "아이디 또는 비밀번호가 틀렸습니다.");
        }
    }

    // 리프레시 (api-gateway)
    @GetMapping("/backend/user/v1/exists/{userId}")
    public ApiResponseDto<String> exists(@PathVariable String userId) {
        if (userService.existsById(userId)) {
            return ApiResponseDto.defaultOk();
        } else {
            return ApiResponseDto.createError("NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }
    }


    @GetMapping(value = "/backend/user/v1/email/{email}")
    Long getUserIdByEmail(@PathVariable("email") String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    // 회원 탈퇴
    @PostMapping("/api/users/delete")
    public ResponseEntity<CustomResponse<String>> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {

        userService.deleteUser(userDetails.getUsername()); // DB의 회원정보 삭제

        return ResponseEntity
                .status(GeneralSuccessCode._OK.getHttpStatus())
                .body(CustomResponse.success(GeneralSuccessCode._OK, "회원 탈퇴 완료"));
    }

    // 유저정보 조회
    @GetMapping("/api/users/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    // 유저정보 수정
    @PutMapping("/api/users/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId,
                                               @ModelAttribute UpdateUserProfileRequestDto dto) {
        try {
            String imageUrl = null;

            if (dto.getImage() != null && !dto.getImage().isEmpty()) {
                imageUrl = s3Service.upload(dto.getImage());
            }

            userService.updateUserProfile(userId, dto, imageUrl);
            return ResponseEntity.ok("프로필 수정 완료");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("프로필 수정 중 오류 발생: " + e.getMessage());
        }
    }

}