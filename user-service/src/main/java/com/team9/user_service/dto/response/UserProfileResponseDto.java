package com.team9.user_service.dto.response;

import com.team9.user_service.global.domain.Category;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserProfileResponseDto {
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private Set<Category> interests;
}
