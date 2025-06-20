package com.lgcns.backend.csanswer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import com.lgcns.backend.ai.dto.AIRequest;
import com.lgcns.backend.ai.dto.AIResponse;
import com.lgcns.backend.ai.dto.AIRequest.OpenAiRequest;
import com.lgcns.backend.csanswer.domain.CSAnswer;
import com.lgcns.backend.csanswer.dto.CSAnswerResponse;
import com.lgcns.backend.csanswer.repository.CSAnswerRepository;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;

@Service
public class AIFeedbackService {

    @Autowired
    private CSAnswerRepository csAnswerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    // openAI API로 Feedback 받기
    @Value("https://api.openai.com/v1/chat/completions")
    String API_URL;
    @Value("${openai.api.key}")
    String API_KEY;
    public CSAnswerResponse.AIFeedbackResponse createAIFeedback(Long answerId, UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        
        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        if (!answer.getUser().getId().equals(user.getId())) {  // 500
            // throw new AccessDeniedException("자신의 답변만 피드백 요청 가능합니다."); 고려 중
            throw new RuntimeException("자신의 답변만 피드백 요청 가능합니다.");
        }

        AIRequest.OpenAiRequest request = AIRequest.OpenAiRequest.builder()
                                                        .model("gpt-4o")
                                                        .temperature(0.7)
                                                        .max_tokens(1000)
                                                        .messages(List.of(
                                                                Map.of("role", "system", "content",
                                                                        "너는 CS 면접을 준비하는 취준생들에게 멘토의 역할을 하는 거야. "
                                                                                + "따라서 CS 원본 질문에 대하여 사용자의 답변에 대해 정밀 분석하여 보완할 점, 잘한 점, 추가 학습할 내용을 제공해줘. "
                                                                                + "각 항목을 구체적으로 제시하고, 반복 학습을 돕기 위한 구체적인 피드백을 제공해줘. "
                                                                                + "피드백을 아래 템플릿에 맞춰 작성해줘. 피드백을 마친 후 추가적인 설명 없이 종료해줘:\n\n"
                                                                                + "## 피드백 보고서\n\n"
                                                                                + "**잘한 점**:\n- [잘한 점을 여기에 AI가 작성]\n\n"
                                                                                + "**보완할 점**:\n- [보완할 점을 여기에 AI가 작성]\n\n"
                                                                                + "**추가 학습 사항**:\n- [AI가 제시하는 학습 사항]\n\n"
                                                                                + "**개선 포인트**:\n- [개선 포인트를 AI가 제공]"),
//                                                                Map.of("role", "system", "content", "CS 면접 답변에 대한 피드백 작성해줘"),
                                                                Map.of("role", "user", "content", "CS 원본 질문: " + answer.getCsQuestion().getContent()),
                                                                Map.of("role", "user", "content", "사용자의 답변" + answer.getContent())))
                                                        .build();
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");
        HttpEntity<OpenAiRequest> entity = new org.springframework.http.HttpEntity<>(request, headers);
        
        AIResponse response = restTemplate.postForObject(API_URL, entity,
                AIResponse.class);
        String feedback = response.getChoices().get(0).getMessage().getContent();

        answer.setFeedback(feedback);
        csAnswerRepository.save(answer);

        return CSAnswerResponse.AIFeedbackResponse.builder()
                                    .csanswer_id(answerId)
                                    .csanswer_feedback(feedback)
                                    .build();
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }

}
