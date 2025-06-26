package com.team9.user_service.service;

import com.team9.user_service.domain.User;
import com.team9.user_service.dto.request.SignUpRequestDto;
import com.team9.user_service.dto.request.UpdateUserProfileRequestDto;
import com.team9.user_service.dto.response.UserProfileResponseDto;
import com.team9.user_service.remote.answer.RemoteAnswerService;
import com.team9.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    // user레파지, PasswordEncoder
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private RemoteAnswerService remoteAnswerService;


    // 회원가입 기능
    public void signUp(SignUpRequestDto dto, String imageurl) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());

        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setName(dto.getName());
        newUser.setNickname(dto.getNickname());
        newUser.setProfileImage(imageurl);
        newUser.setRole("ROLE_USER"); // userRole을 User로 지정
        newUser.setInterests(dto.getInterests());

        userRepository.save(newUser);

    }

    // 로그인
    public boolean validateCredentials(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(value -> passwordEncoder.matches(password, value.getPassword())).orElse(false);
    }

    // 리프레시
    public boolean existsById(String userId) {
        return userRepository.existsByEmail(userId);
    }

    // 회원 탈퇴 기능
    @Transactional
    public void deleteUser(String email) {
        // 로깅을 위한 Logger 객체
        Logger logger = LoggerFactory.getLogger(getClass());

        // 사용자가 존재하는지 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        try {
            logger.info("사용자 삭제 시작: " + user.getEmail());

            // 답변 삭제 요청
            remoteAnswerService.deleteAnswersByUser(user.getEmail());

            // 사용자 삭제
            userRepository.delete(user);
            logger.info("사용자 삭제 완료: " + user.getEmail());

        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            logger.error("사용자 삭제 중 오류 발생: " + email, e);
            throw new RuntimeException("연관된 데이터 삭제 중 오류 발생", e);
        }
    }

    // 프로필 조회
    public UserProfileResponseDto getUserProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        return UserProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .interests(user.getInterests())
                .build();
    }

    @Transactional
    public void updateUserProfile(String email, UpdateUserProfileRequestDto dto, String imageUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        if (imageUrl != null) {
            user.setProfileImage(imageUrl);
        }

        if (dto.getInterests() != null) {
            user.setInterests(dto.getInterests());
        }

        if (dto.getCurrentPassword() != null && dto.getNewPassword() != null) {
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
            }

            if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
                throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
            }

            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(user);
    }


}
