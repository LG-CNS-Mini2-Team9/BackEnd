package com.lgcns.backend.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SignUpRequestDto {
    String email;
    String password;
    String name;
    String nickname;
//    @JsonProperty("profile_image")
//    String profileImage;
    String role;
    
    MultipartFile image; // 회원이 업로드한 이미지
}
