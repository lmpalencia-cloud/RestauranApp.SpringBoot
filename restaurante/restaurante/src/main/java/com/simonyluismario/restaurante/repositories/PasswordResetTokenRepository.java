package com.simonyluismario.restaurante.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simonyluismario.restaurante.models.PasswordResetToken;

public interface PasswordResetTokenRepository  extends JpaRepository<PasswordResetToken, Long>{

    Optional<PasswordResetToken> findByToken(String token);
}
