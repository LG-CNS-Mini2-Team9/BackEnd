package com.lgcns.backend.user.repository;

import com.lgcns.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);
}