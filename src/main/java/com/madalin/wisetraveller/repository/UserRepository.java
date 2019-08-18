package com.madalin.wisetraveller.repository;

import com.madalin.wisetraveller.model.User;
import com.madalin.wisetraveller.model.enums.TipUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndTipUser(String email, TipUser tipUser);
    Optional<User> findByUserProvidedIdAndTipUser(String userProvidedId, TipUser tipUser);
}