package com.team9.answer_service.service;

import com.team9.answer_service.domain.repository.LikeRepository;
import com.team9.answer_service.remote.user.RemoteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RemoteUserService remoteUserService;

//    public Long countMyAnswerLikes(UserDetails userDetails) {
//        Long userId = getUserIdFromDetails(userDetails);
//
//        return likeRepository.countAllLikesReceivedByUser(userId);
//    }
    public Long countMyAnswerLikes() {
//        Long userId = getUserIdFromDetails(userDetails);
        Long userId = 1L;

        return likeRepository.countAllLikesReceivedByUser(userId);
    }


    private Long getUserIdFromDetails(UserDetails userDetails) {
        // TODO: remoteUserService에서 findByEmail
        Long id = remoteUserService.getUserIdByEmail(userDetails.getUsername());
        if (id == null) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }
        return id;
    }

}
