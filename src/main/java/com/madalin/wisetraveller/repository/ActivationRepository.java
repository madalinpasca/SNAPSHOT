package com.madalin.wisetraveller.repository;

import com.madalin.wisetraveller.model.Activation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationRepository extends JpaRepository<Activation, Long> {
    Optional<Activation> findByUserIdAndUuid(Long id, String uuid);
    Optional<Activation> findByUserId(Long id);
}
