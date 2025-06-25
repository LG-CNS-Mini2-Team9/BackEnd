package com.team9.question_service.repository;

import com.team9.common.domain.Category;
import com.team9.question_service.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByCategory(Category category, Pageable pageable);

    Question findFirstByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    // 네이티브 쿼리 사용하는 이유: JPQL 표준에 DB에서 직접 무작위 정렬을 하는 기능이 없기 때문
    /**
     * 주어진 카테고리 목록에 속하면서, 제외할 ID 목록에 포함되지 않는 질문을 랜덤으로 1개 조회합니다.
     *
     * @param categories 관심 카테고리 목록
     * @param excludedIds 제외할 질문 ID 목록 (이미 답변한 질문)
     * @return 랜덤으로 선택된 Question (Optional)
     */
    @Query(value = "SELECT * FROM question q " +
            "WHERE q.category IN :categories AND q.id NOT IN :excludedIds AND q.is_active = true " +
            "ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomQuestionByCategoriesAndNotInIds(
            @Param("categories") List<String> categories,
            @Param("excludedIds") List<Long> excludedIds
    );

    /**
     * 모든 질문 중에서 제외할 ID 목록에 포함되지 않는 질문을 랜덤으로 1개 조회합니다. (Fallback 용)
     *
     * @param excludedIds 제외할 질문 ID 목록
     * @return 랜덤으로 선택된 Question (Optional)
     */
    @Query(value = "SELECT * FROM question q " +
            "WHERE q.id NOT IN :excludedIds AND q.is_active = true " +
            "ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomQuestionNotInIds(@Param("excludedIds") List<Long> excludedIds);
}