package com.team9.api_gateway.auth.remote.user.dto;

import com.team9.api_gateway.auth.dto.LoginDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateUserDto {
    private String userId;

    private String password;

    public static ValidateUserDto of(LoginDto loginDto) {
        ValidateUserDto validateUserDto = new ValidateUserDto();

        validateUserDto.userId = loginDto.getUserId();
        validateUserDto.password = loginDto.getPassword();

        return validateUserDto;
    }
}
