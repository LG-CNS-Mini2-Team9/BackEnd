package com.team9.statistic_service.remote.answer.dto;

import com.team9.statistic_service.domain.enums.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CSStatisticResponse {
    private Long csquestion_id;
    private Category csquestion_category;
    private Long csanswer_id;
    private int csanswer_score;
}