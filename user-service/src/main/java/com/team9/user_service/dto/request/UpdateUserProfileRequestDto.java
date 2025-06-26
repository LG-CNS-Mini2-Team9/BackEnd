package com.team9.user_service.dto.request;

import com.team9.user_service.global.domain.Category;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class UpdateUserProfileRequestDto {
    private String name;
    private String nickname;
    private MultipartFile image;
    private Set<Category> interests;
    private String currentPassword;
    private String newPassword;
}
