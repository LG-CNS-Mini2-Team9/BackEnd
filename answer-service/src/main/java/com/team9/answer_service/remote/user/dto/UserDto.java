package com.team9.answer_service.remote.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor

public class UserDto {
    @Getter
    @Setter
    public class CSAnswerUserDto{
        private Long id;
        private String nickname;
    }
}
