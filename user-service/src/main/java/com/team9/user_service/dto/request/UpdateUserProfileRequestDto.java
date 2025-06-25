package com.team9.user_service.dto.request;

import com.team9.user_service.global.domain.Category;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserProfileRequestDto {
    private String name;
    private String nickname;
    private String profileImage;
    private Set<Category> interests;
}
