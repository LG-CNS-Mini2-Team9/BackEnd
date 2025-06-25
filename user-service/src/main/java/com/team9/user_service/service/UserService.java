package com.team9.user_service.service;

//import com.lgcns.backend.csanswer.repository.CSAnswerRepository;


import com.team9.user_service.domain.User;
import com.team9.user_service.dto.request.LoginRequestDto;
import com.team9.user_service.dto.request.SignUpRequestDto;
import com.team9.user_service.dto.request.UpdateUserRequestDto;
import com.team9.user_service.global.code.GeneralErrorCode;
import com.team9.user_service.global.code.GeneralSuccessCode;
import com.team9.user_service.global.response.CustomResponse;
import com.team9.user_service.repository.UserRepository;
import com.team9.user_service.security.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    // user레파지, PasswordEncoder, jwt유틸 와이어링
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    private S3Service s3Service;
//    @Autowired
//    private CSAnswerRepository csAnswerRepository;

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

    // 로그인 기능
    public CustomResponse login(LoginRequestDto dto) {
        try {
            // 이메일을 username으로 취급함
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(), dto.getPassword()));

            // 인증 성공시 JWT 발행
            String accessToken = jwtUtil.createAccessToken(authentication.getName());
            String refreshToken = jwtUtil.createRefreshToken(authentication.getName()); // 리프레시 토큰 발급 추가
            System.out.println("✅ 인증 성공: " + authentication.getName());


            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

            String profileImgUrl = user.getProfileImage();
            Map<String, String> map = new HashMap<>();

            map.put("accesstoken", accessToken);
            map.put("refreshtoken", refreshToken);
            map.put("email", user.getEmail());
            map.put("name", user.getName());
            map.put("nickname", user.getNickname());
            map.put("profileImgUrl", profileImgUrl);

            return CustomResponse.success(GeneralSuccessCode._OK, map);

        } catch (AuthenticationException e) {
            System.out.println("❌ 인증 실패: " + e.getMessage());
            return CustomResponse.fail(GeneralErrorCode._UNAUTHORIZED, "로그인 실패: 잘못된 이메일 또는 비밀번호");
        }
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

//            csAnswerRepository.deleteByUser(user);
//            logger.info("CSAnswer 삭제 완료: " + user.getEmail());

            // 사용자 삭제
            userRepository.delete(user);
            logger.info("사용자 삭제 완료: " + user.getEmail());

        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            logger.error("사용자 삭제 중 오류 발생: " + email, e);
            throw new RuntimeException("연관된 데이터 삭제 중 오류 발생", e);
        }
    }

    // 회원 정보 변경 기능
    public CustomResponse updateUser(String email, UpdateUserRequestDto dto) throws IOException {
        // 사용자 존재여부 판단 - 굳이 필요 없긴하다
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


        // 1. 닉네임은 기존과 다른지 + 중복 검사까지 수행
        // 변경 정보의 닉네임이 null이 아니면
        if (dto.getNickname() != null) {
            if (!user.getNickname().equals(dto.getNickname())) { // 기존 닉네임과 수정하려는 닉네임과 다르다면
                if (userRepository.existsByNickname(dto.getNickname())) { // 만일 닉네임이 이미 존재시
                    return CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
                }
                else {
                    user.setNickname(dto.getNickname());
                }
            } else { // 같다면
                return CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "기존 닉네임과 같습니다.");
            }
        } else {
            return CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "모든 정보를 입력해주세요.");
        }

        // 2. 비밀번호 변경 로직 - 기존 것이 일치하는지 -> 일치하면 새 비밀번호로 변경
        if (dto.getCurrentPassword() != null && dto.getNewPassword() != null) { // 만일 기존 비밀번호와 새 비밀번호가 존재시
            // 기존 비밀번호가 일치하는지 확인
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return CustomResponse.fail(GeneralErrorCode._UNAUTHORIZED, "기존 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호가 기존 비밀번호와 같은지 확인
            if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
                return CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
            }
            // 변경 진행
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        } else {
            return CustomResponse.fail(GeneralErrorCode._BAD_REQUEST, "모든 정보를 입력해주세요222.");
        }

        // 프로필 이미지
        if (dto.getNewProfileimage() != null) {
            String imageUrl = s3Service.upload(dto.getNewProfileimage());
            user.setProfileImage(imageUrl);
        }

        userRepository.save(user);
        return CustomResponse.ok("회원 정보 수정 완료");

    }

    public boolean existsById(String userId) {
        return userRepository.existsByEmail(userId);
    }

}
