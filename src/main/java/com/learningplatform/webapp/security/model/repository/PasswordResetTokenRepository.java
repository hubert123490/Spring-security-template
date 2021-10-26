package com.learningplatform.webapp.security.model.repository;

import com.learningplatform.webapp.security.model.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByToken(String token);
    PasswordResetTokenEntity findByUserEntity_Email(String email);
}
