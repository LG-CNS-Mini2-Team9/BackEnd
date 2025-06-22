package com.team9.api_gateway.auth.service;

import com.team9.api_gateway.auth.dto.LoginDto;
import com.team9.api_gateway.auth.dto.RefreshDto;
import com.team9.api_gateway.auth.jwt.TokenGenerator;
import com.team9.api_gateway.auth.jwt.TokenValidator;
import com.team9.api_gateway.auth.jwt.dto.TokenDto;
import com.team9.api_gateway.auth.remote.user.RemoteUserService;
import com.team9.api_gateway.auth.remote.user.dto.ValidateUserDto;
import com.team9.api_gateway.common.dto.ApiResponseDto;
import com.team9.api_gateway.common.exception.BadParameter;
import com.team9.api_gateway.common.exception.NotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("api-gateway 로그인/갱신 테스트")
class AuthServiceTest {

    @Mock
    private TokenGenerator jwtTokenGenerator;

    @Mock
    private TokenValidator jwtTokenValidator;

    @Mock
    private RemoteUserService remoteUserService;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        private LoginDto loginDto;
        private TokenDto.AccessRefreshToken expectedToken;

        @BeforeEach
        void setUp() {
            loginDto = new LoginDto();
            loginDto.setUserId("testUser");
            loginDto.setPassword("testPassword");

            TokenDto.JwtToken access = new TokenDto.JwtToken("accessToken", 1000);
            TokenDto.JwtToken refresh = new TokenDto.JwtToken("refreshToken", 1000);
            expectedToken = new TokenDto.AccessRefreshToken(access, refresh);
        }

        @Test
        @DisplayName("성공: 유효한 사용자 정보로 로그인")
        void login_Success() {
            // given
            ApiResponseDto<String> successResponse =  ApiResponseDto.defaultOk();
            given(remoteUserService.validate(any(ValidateUserDto.class))).willReturn(successResponse);
            given(jwtTokenGenerator.generateAccessRefreshToken(eq("testUser"), eq("WEB")))
                    .willReturn(expectedToken);

            // when
            TokenDto.AccessRefreshToken result = authService.login(loginDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccess().getToken()).isEqualTo("accessToken");
            assertThat(result.getRefresh().getToken()).isEqualTo("refreshToken");

            verify(remoteUserService, times(1)).validate(any(ValidateUserDto.class));
            verify(jwtTokenGenerator, times(1)).generateAccessRefreshToken("testUser", "WEB");
        }

        @Test
        @DisplayName("실패: 잘못된 사용자 정보로 로그인")
        void login_Fail_InvalidCredentials() {
            // given
            ApiResponseDto<String> failResponse = ApiResponseDto.createError("NotFound", "user not found");
            given(remoteUserService.validate(any(ValidateUserDto.class))).willReturn(failResponse);

            // when & then
            assertThatThrownBy(() -> authService.login(loginDto))
                    .isInstanceOf(BadParameter.class);

            verify(remoteUserService, times(1)).validate(any(ValidateUserDto.class));
            verify(jwtTokenGenerator, times(0)).generateAccessRefreshToken(any(), any());
        }
    }

    @Nested
    @DisplayName("토큰 갱신 테스트")
    class RefreshTest {

        private RefreshDto refreshDto;
        private TokenDto.AccessToken expectedAccessToken;

        @BeforeEach
        void setUp() {
            refreshDto = new RefreshDto();
            refreshDto.setToken("refreshToken");

            TokenDto.JwtToken access = new TokenDto.JwtToken("newAccessToken", 1000);
            expectedAccessToken = new TokenDto.AccessToken(access);
        }

        @Test
        @DisplayName("성공: 유효한 리프레시 토큰으로 액세스 토큰 갱신")
        void refresh_Success() {
            // given
            given(jwtTokenValidator.validateJwtToken("refreshToken")).willReturn("testUser");
            ApiResponseDto<String> existsResponse = ApiResponseDto.defaultOk();
            given(remoteUserService.exists("testUser")).willReturn(existsResponse);
            given(jwtTokenGenerator.generateAccessToken("testUser", "WEB")).willReturn(expectedAccessToken);

            // when
            TokenDto.AccessToken result = authService.refresh(refreshDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccess().getToken()).isEqualTo("newAccessToken");

            verify(jwtTokenValidator, times(1)).validateJwtToken("refreshToken");
            verify(remoteUserService, times(1)).exists("testUser");
            verify(jwtTokenGenerator, times(1)).generateAccessToken("testUser", "WEB");
        }

        @Test
        @DisplayName("실패: 유효하지 않은 토큰")
        void refresh_Fail_InvalidToken() {
            // given
            given(jwtTokenValidator.validateJwtToken("refreshToken")).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authService.refresh(refreshDto))
                    .isInstanceOf(BadParameter.class);

            verify(jwtTokenValidator, times(1)).validateJwtToken("refreshToken");
            verify(remoteUserService, times(0)).exists(any());
            verify(jwtTokenGenerator, times(0)).generateAccessToken(any(), any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자")
        void refresh_Fail_UserNotExists() {
            // given
            given(jwtTokenValidator.validateJwtToken("refreshToken")).willReturn("testUser");
            ApiResponseDto<String> notExistsResponse = ApiResponseDto.createError("NotFound", "user not found");
            given(remoteUserService.exists("testUser")).willReturn(notExistsResponse);

            // when & then
            assertThatThrownBy(() -> authService.refresh(refreshDto))
                    .isInstanceOf(NotFound.class);

            verify(jwtTokenValidator, times(1)).validateJwtToken("refreshToken");
            verify(remoteUserService, times(1)).exists("testUser");
            verify(jwtTokenGenerator, times(0)).generateAccessToken(any(), any());
        }
    }
}
