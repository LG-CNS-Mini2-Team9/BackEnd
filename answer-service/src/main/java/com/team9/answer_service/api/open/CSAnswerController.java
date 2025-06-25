package com.team9.answer_service.api.open;

import com.team9.answer_service.domain.CSAnswer;
import com.team9.answer_service.domain.dto.CSAnswerRequest;
import com.team9.answer_service.domain.dto.CSAnswerResponse;
import com.team9.answer_service.domain.repository.CSAnswerRepository;
import com.team9.answer_service.global.code.GeneralSuccessCode;
import com.team9.answer_service.global.response.CustomResponse;
import com.team9.answer_service.remote.ai.dto.FeedbackScoreResponseDto;
import com.team9.answer_service.service.CSAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/answers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CSAnswerController {
    private final CSAnswerService csAnswerService;
    private final CSAnswerRepository csAnswerRepository;

    // 답변 작성
    @PostMapping
    public ResponseEntity<CustomResponse<CSAnswerResponse.CSAnswerDetailResponse>> createAnswer(
            @RequestBody CSAnswerRequest.CSAnswerCreateRequest request,
            @RequestHeader(value = "X-User-Id") Long userId) {

        CSAnswerResponse.CSAnswerDetailResponse response = csAnswerService.createAnswer(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.created(response));
    }

    // 내 답변 리스트 조회 (모든/질문별)
    @GetMapping(value = "/my")
    public ResponseEntity<CustomResponse<Page<CSAnswerResponse.CSAnswerListResponse>>> readMyAnswerList(
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Long questionId
    ) {
        Order order = Order.desc("id");
        Sort sort = Sort.by(order);
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<CSAnswerResponse.CSAnswerListResponse> responseList = csAnswerService.getMyAnswerList(userId, pageable, questionId);
        return ResponseEntity.ok(CustomResponse.ok(responseList));
    }

    // 질문별 답변 리스트 조회
    @GetMapping(value = "/{questionId}")
    public ResponseEntity<CustomResponse<Page<CSAnswerResponse.CSAnswerListResponse>>> readAnswerList(
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @PathVariable Long questionId
    ) {
        Order order = Order.desc("id");
        Sort sort = Sort.by(order);
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<CSAnswerResponse.CSAnswerListResponse> responseList = csAnswerService.getAnswerList(userId, pageable, questionId);
        return ResponseEntity.ok(CustomResponse.ok(responseList));
    }

    // 특정 답변 조회 (페이지, 특정 질문)
    @GetMapping("/detail/{answerId}")
    public ResponseEntity<CustomResponse<CSAnswerResponse.CSAnswerDetailResponse>> readAnswer(
            @PathVariable Long answerId,
            @RequestHeader(value = "X-User-Id") Long userId) {
        CSAnswerResponse.CSAnswerDetailResponse response = csAnswerService.getAnswerDetail(answerId, userId);
        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 답변 수정
    @PostMapping("/{answerId}/edit")
    public ResponseEntity<CustomResponse<CSAnswerResponse.CSAnswerDetailResponse>> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody CSAnswerRequest.CSAnswerUpdate request,
            @RequestHeader(value = "X-User-Id") Long userId) {

        CSAnswerResponse.CSAnswerDetailResponse response = csAnswerService.updateAnswer(answerId, request, userId);

        // 피드백 생성
        FeedbackScoreResponseDto feedback = csAnswerService.createFeedback(response.getCsquestion_id(), response.getCsanswer_id(), response.getCsanswer_content());
        response.setCsanswer_score(feedback.getScore());
        return ResponseEntity.ok(CustomResponse.ok(response));
    }

    // 답변 삭제
    @PostMapping("/{answerId}/delete")
    public ResponseEntity<CustomResponse<Void>> deleteAnswer(@PathVariable Long answerId, @RequestHeader(value = "X-User-Id") Long userId) {
        csAnswerService.deleteAnswer(answerId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(CustomResponse.success(GeneralSuccessCode._DELETED, null));
    }

    @GetMapping("/test")
    public List<CSAnswer> test() {
        return csAnswerRepository.findAll();
    }
}


