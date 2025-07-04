package com.team9.api_gateway.auth.remote.user;

import com.team9.api_gateway.auth.remote.user.dto.ValidateUserDto;
import com.team9.api_gateway.common.dto.ApiResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/backend/user/v1")
public interface RemoteUserService {
    @GetMapping("/exists/{userId}")
    public ApiResponseDto<String> exists(@PathVariable String userId);

    @PostMapping("/validate")
    public ApiResponseDto<String> validate(@RequestBody ValidateUserDto validateUserDto);

    @GetMapping("/email/{email}")
    Long getUserIdByEmail(@PathVariable String email);
}
