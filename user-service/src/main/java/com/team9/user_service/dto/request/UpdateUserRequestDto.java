package com.lgcns.backend.user.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserRequestDto {
//    @JsonProperty("current_password")
    String currentPassword;
//    @JsonProperty("new_password")
    String newPassword;
    String nickname;
//    @JsonProperty("profile_image")
//    String profileImage;
    MultipartFile newProfileimage; // 회원이 업로드한 이미지
}
