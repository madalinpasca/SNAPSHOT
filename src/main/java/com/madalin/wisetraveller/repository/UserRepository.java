package com.madalin.wisetraveller.repository;

import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.model.enums.TipUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUserProvidedIdAndTipUser(String userProvidedId, TipUser tipUser);
}