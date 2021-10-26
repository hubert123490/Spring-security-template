package com.learningplatform.webapp.security.model.repository;

import com.learningplatform.webapp.security.model.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
