package com.learningplatform.webapp.security.eventclasses;

import com.learningplatform.webapp.security.model.entity.AuthorityEntity;
import com.learningplatform.webapp.security.model.entity.RoleEntity;
import com.learningplatform.webapp.security.model.entity.UserEntity;
import com.learningplatform.webapp.security.model.repository.AuthorityRepository;
import com.learningplatform.webapp.security.model.repository.RoleRepository;
import com.learningplatform.webapp.security.model.repository.UserRepository;
import com.learningplatform.webapp.security.shared.Roles;
import com.learningplatform.webapp.security.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class InitialUsersSetup {
    private final Logger logger = LoggerFactory.getLogger(InitialUsersSetup.class);
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;


    public InitialUsersSetup(AuthorityRepository authorityRepository,
                             RoleRepository roleRepository, Utils utils,
                             BCryptPasswordEncoder bCryptPasswordEncoder,
                             UserRepository userRepository) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("From Application ready event... ");

        logger.info("Creating authorities... ");
        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        logger.info("Creating roles... ");
        createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        UserEntity admin = userRepository.findByEmail("hubert1234.91@gmail.com");

        if (admin != null) {
            logger.info("Admin already created... ");
        } else {
            logger.info("Creating admin... ");
            UserEntity adminUser = new UserEntity();
            adminUser.setFirstName("Hubert");
            adminUser.setLastName("Kowalczyk");
            adminUser.setEmail("hubert1234.91@gmail.com");
            adminUser.setEmailVerificationStatus(true);
            adminUser.setUserId(utils.generateUserId(30));
            adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("admin"));
            adminUser.setRoles(Collections.singletonList(roleAdmin));
            userRepository.save(adminUser);
        }
    }

    private AuthorityEntity createAuthority(String name) {
        AuthorityEntity authorityEntity = authorityRepository.findByName(name);
        if (authorityEntity == null) {
            authorityEntity = new AuthorityEntity(name);
            authorityRepository.save(authorityEntity);
        }
        return authorityEntity;
    }

    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }
}
