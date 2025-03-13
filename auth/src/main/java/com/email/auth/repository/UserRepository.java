package com.email.auth.repository;

import com.email.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 정보 저장소 인터페이스입니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param username 사용자 ID
     * @return 사용자 정보
     */
    Optional<User> findByUsername(String username);
}
