package com.lgcns.backend.unit.csanswer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lgcns.backend.csanswer.dto.CSAnswerRequest;
import com.lgcns.backend.csanswer.dto.CSAnswerResponse;
import com.lgcns.backend.csanswer.repository.CSAnswerRepository;
import com.lgcns.backend.csanswer.service.CSAnswerService;
import com.lgcns.backend.csanswer.domain.CSAnswer;
import com.lgcns.backend.csquestion.domain.CSQuestion;
import com.lgcns.backend.csquestion.repository.CSQuestionRepository;
import com.lgcns.backend.global.domain.Category;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class CSAnswerServiceTest {

    @Autowired
    private CSAnswerService csAnswerService;

    @MockBean
    private CSAnswerRepository csAnswerRepository;

    @MockBean
    private CSQuestionRepository csQuestionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetails userDetails;

    private User user;
    private CSQuestion question;
    private CSAnswer answer;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(2L)
                .email("user1@example.com")
                .nickname("길동이")
                .build();

        question = CSQuestion.builder()
                .id(1L)
                .category(Category.기타)
                .content("CSAnswerService 테스트용 질문입니다. 답변해주세요.")
                .build();

        answer = new CSAnswer();
        answer.setId(1L);
        answer.setContent("CSAnswerService 테스트에 대한 답변입니다.");
        answer.setCreatedAt(LocalDateTime.now());
        answer.setFeedback("아직 피드백 없음");
        answer.setUser(user);
        answer.setCsQuestion(question);

        // Mock userDetails to return user's email as username
        given(userDetails.getUsername()).willReturn(user.getEmail());
    }

    @DisplayName("답변 생성 - Success")
    @Test
    void createAnswer_success() {
        CSAnswerRequest.CSAnswerCreateRequest request = CSAnswerRequest.CSAnswerCreateRequest.builder()
                .csquestion_id(question.getId())
                .csanswer_content("new answer")
                .build();
        given(csQuestionRepository.findById(question.getId())).willReturn(Optional.of(question));
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.save(any(CSAnswer.class))).willAnswer(invocation -> {
            CSAnswer saved = invocation.getArgument(0);
            saved.setId(10L); // simulate DB assigning id
            return saved;
        });

        CSAnswerResponse.CSAnswerDetailResponse response = csAnswerService.createAnswer(request, userDetails);

        assertThat(response.getCsanswer_id()).isNotNull();
        assertThat(response.getCsanswer_content()).isEqualTo("new answer");
        assertThat(response.getCsquestion_id()).isEqualTo(question.getId());
    }

    @DisplayName("답변 생성 - 존재하지 않는 questionId일 경우 예외 발생")
    @Test
    void createAnswer_fail_whenQuestionNotFound() {
        CSAnswerRequest.CSAnswerCreateRequest request = CSAnswerRequest.CSAnswerCreateRequest.builder()
                .csquestion_id(999L)  // 존재하지 않는 질문 ID
                .csanswer_content("new answer")
                .build();

        given(csQuestionRepository.findById(999L)).willReturn(Optional.empty());
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        assertThatThrownBy(() -> csAnswerService.createAnswer(request, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("질문이 존재하지 않습니다.");
    }

    @DisplayName("답변 리스트 조회 - questionId를 넘기지 않는 경우 (Success)")
    @Test
    void getAnswerList_withoutQuestionId_success() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<CSAnswer> answerPage = new PageImpl<>(List.of(answer));

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findAllByUser(user, pageable)).willReturn(answerPage);

        // when
        Page<CSAnswerResponse.CSAnswerListResponse> responsePage = csAnswerService.getAnswerList(userDetails, pageable,
                null);

        // then
        assertThat(responsePage.getContent()).hasSize(1);
        CSAnswerResponse.CSAnswerListResponse resp = responsePage.getContent().get(0);
        assertThat(resp.getUser_nickname()).isEqualTo(user.getNickname());
        assertThat(resp.getCsanswer_content()).isEqualTo(answer.getContent());
        assertThat(resp.getCsquestion_id()).isEqualTo(question.getId());
    }

    @DisplayName("답변 리스트 조회 - questionId를 넘기는 경우 (Success)")
    @Test
    void getAnswerList_withQuestionId_success() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<CSAnswer> answerPage = new PageImpl<>(List.of(answer));

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csQuestionRepository.findById(question.getId())).willReturn(Optional.of(question));
        given(csAnswerRepository.findAllByUserAndCsQuestion(user, question, pageable)).willReturn(answerPage);

        // when
        Page<CSAnswerResponse.CSAnswerListResponse> responsePage = csAnswerService.getAnswerList(userDetails, pageable,
                question.getId());

        // then
        assertThat(responsePage.getContent()).hasSize(1);
        CSAnswerResponse.CSAnswerListResponse resp = responsePage.getContent().get(0);
        assertThat(resp.getCsquestion_id()).isEqualTo(question.getId());
        assertThat(resp.getCsanswer_content()).isEqualTo(answer.getContent());
    }

    @DisplayName("답변 리스트 조회 - 존재하지 않는 questionId일 경우 예외 발생")
    @Test
    void getAnswerList_fail_whenQuestionNotFound() {
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csQuestionRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> csAnswerService.getAnswerList(userDetails, pageable, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("질문이 존재하지 않습니다.");
    }

    @DisplayName("답변 상세 조회 - Success")
    @Test
    void getAnswerDetail_success() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(answer.getId())).willReturn(Optional.of(answer));

        CSAnswerResponse.CSAnswerDetailResponse response = csAnswerService.getAnswerDetail(answer.getId(), userDetails);

        assertThat(response.getCsanswer_id()).isEqualTo(answer.getId());
        assertThat(response.getCsanswer_content()).isEqualTo(answer.getContent());
    }

    @DisplayName("답변 상세 조회 - 자신의 답변이 아닌 경우 예외 발생")
    @Test
    void getAnswerDetail_fail_whenNotOwner() {
        User otherUser = User.builder().id(3L).email("other@example.com").nickname("other").build();
        answer.setUser(otherUser);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(answer.getId())).willReturn(Optional.of(answer));

        assertThatThrownBy(() -> csAnswerService.getAnswerDetail(answer.getId(), userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("자신의 답변만 조회할 수 있습니다.");
    }

    @DisplayName("답변 상세 조회 - 존재하지 않는 answerId일 경우 예외 발생")
    @Test
    void getAnswerDetail_fail_whenAnswerNotFound() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> csAnswerService.getAnswerDetail(999L, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("답변이 존재하지 않습니다.");
    }

    @DisplayName("답변 수정 - Success")
    @Test
    void updateAnswer_success() {
        CSAnswerRequest.CSAnswerUpdate updateRequest = CSAnswerRequest.CSAnswerUpdate.builder()
                .csanswer_content("updated content")
                .build();

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(answer.getId())).willReturn(Optional.of(answer));
        given(csAnswerRepository.save(any(CSAnswer.class))).willAnswer(invocation -> invocation.getArgument(0));

        CSAnswerResponse.CSAnswerDetailResponse response = csAnswerService.updateAnswer(answer.getId(), updateRequest,
                userDetails);

        assertThat(response.getCsanswer_content()).isEqualTo("updated content");
    }

    @DisplayName("답변 수정 - 자신의 답변이 아닌 경우 예외 발생")
    @Test
    void updateAnswer_fail_whenNotOwner() {
        User otherUser = User.builder().id(3L).email("other@example.com").nickname("other").build();
        answer.setUser(otherUser);

        CSAnswerRequest.CSAnswerUpdate updateRequest = CSAnswerRequest.CSAnswerUpdate.builder()
                .csanswer_content("updated content")
                .build();

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(answer.getId())).willReturn(Optional.of(answer));

        assertThatThrownBy(() -> csAnswerService.updateAnswer(answer.getId(), updateRequest, userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("자신의 답변만 수정할 수 있습니다.");
    }

    @DisplayName("답변 수정 - 존재하지 않는 answerId일 경우 예외 발생")
    @Test
    void updateAnswer_fail_whenAnswerNotFound() {
        CSAnswerRequest.CSAnswerUpdate updateRequest = CSAnswerRequest.CSAnswerUpdate.builder()
                .csanswer_content("updated content")
                .build();

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> csAnswerService.updateAnswer(999L, updateRequest, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("답변이 존재하지 않습니다.");
    }

    @DisplayName("답변 삭제 - Success")
    @Test
    void deleteAnswer_success() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(answer.getId())).willReturn(Optional.of(answer));
        willDoNothing().given(csAnswerRepository).deleteById(answer.getId());

        csAnswerService.deleteAnswer(answer.getId(), userDetails);

        then(csAnswerRepository).should().deleteById(answer.getId());
    }

    @DisplayName("답변 삭제 - 자신의 답변이 아닌 경우 예외 발생")
    @Test
    void deleteAnswer_fail_whenNotOwner() {
        User otherUser = User.builder().id(3L).email("other@example.com").nickname("other").build();
        answer.setUser(otherUser);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(answer.getId())).willReturn(Optional.of(answer));

        assertThatThrownBy(() -> csAnswerService.deleteAnswer(answer.getId(), userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("자신의 답변만 삭제할 수 있습니다.");
    }

    @DisplayName("답변 삭제 - 존재하지 않는 answerId일 경우 예외 발생")
    @Test
    void deleteAnswer_fail_whenAnswerNotFound() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(csAnswerRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> csAnswerService.deleteAnswer(999L, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("답변이 존재하지 않습니다.");
    }
}
