package com.team9.answer_service.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeResponse {
    private Long count;
    private boolean isLiked;
}
