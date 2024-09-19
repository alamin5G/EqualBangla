package com.goonok.equalbangla.repository;


import com.goonok.equalbangla.model.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiryDate < :now AND (vt.isVerified = false OR vt.isSubmitted = false)")
    void deleteExpiredTokens(@org.springframework.data.repository.query.Param("now") LocalDateTime now);


}