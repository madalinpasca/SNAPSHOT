package com.madalin.wisetraveller.repository;

import com.madalin.wisetraveller.model.ResetPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, Long> {
    Optional<ResetPasswordRequest> findByUserId(Long id);
    Optional<ResetPasswordRequest> findByUserIdAndUuid(Long id, String uuid);
}
