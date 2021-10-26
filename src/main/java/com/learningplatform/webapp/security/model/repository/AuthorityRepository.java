package com.learningplatform.webapp.security.model.repository;

import com.learningplatform.webapp.security.model.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {
    AuthorityEntity findByName(String name);
}
