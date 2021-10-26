package com.learningplatform.webapp.security.logic.serviceImpl;

import com.learningplatform.webapp.security.UserPrincipal;
import com.learningplatform.webapp.security.constants.SecurityConstants;
import com.learningplatform.webapp.security.exceptions.ErrorMessages;
import com.learningplatform.webapp.security.exceptions.UserServiceException;
import com.learningplatform.webapp.security.logic.service.UserService;
import com.learningplatform.webapp.security.mail.EmailSender;
import com.learningplatform.webapp.security.model.dto.UserDto;
import com.learningplatform.webapp.security.model.entity.PasswordResetTokenEntity;
import com.learningplatform.webapp.security.model.entity.RoleEntity;
import com.learningplatform.webapp.security.model.entity.UserEntity;
import com.learningplatform.webapp.security.model.repository.PasswordResetTokenRepository;
import com.learningplatform.webapp.security.model.repository.RoleRepository;
import com.learningplatform.webapp.security.model.repository.UserRepository;
import com.learningplatform.webapp.security.shared.Utils;
import io.jsonwebtoken.ExpiredJwtException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSender emailSender;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public UserServiceImpl(UserRepository userRepository, Utils utils,
                           BCryptPasswordEncoder bCryptPasswordEncoder, EmailSender emailSender,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailSender = emailSender;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exists");

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        // Set roles
        Collection<RoleEntity> roleEntities = new HashSet<>();
        if(user.getRoles() != null) {
            for (String role : user.getRoles()) {
                RoleEntity roleEntity = roleRepository.findByName(role);
                if (roleEntity != null) {
                    roleEntities.add(roleEntity);
                }
            }
        }

        userEntity.setRoles(roleEntities);

        // Send verification email
        try {
            emailSender.sendVerificationEmail(userEntity, SecurityConstants.SITE_URL);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        UserEntity storedUserDetails = userRepository.save(userEntity);

        return modelMapper.map(storedUserDetails, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipal(userEntity);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UsernameNotFoundException("User with ID: " + userId + " not found");

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        return modelMapper.map(updatedUserDetails, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            returnValue.add(modelMapper.map(userEntity, UserDto.class));
        }

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token){
        boolean returnValue = false;
        boolean hasTokenExpired;

        // Find user by token
        UserEntity userEntity = userRepository.findByEmailVerificationToken(token);

        if (userEntity != null) {
            try{
                hasTokenExpired = Utils.hasTokenExpired(token);
            }catch (ExpiredJwtException e){
                userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userEntity.getUserId()));
                userEntity.setEmailVerificationStatus(false);
                try {
                    emailSender.sendVerificationEmail(userEntity, SecurityConstants.SITE_URL);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                userRepository.save(userEntity);
                return false;
            }

            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(true);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return false;
        }

        String token = utils.generatePasswordResetToken(userEntity.getUserId());
        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();

        if (userEntity.getPasswordResetTokenEntity() != null) {
            passwordResetTokenEntity = passwordResetTokenRepository.findByUserEntity_Email(userEntity.getEmail());
            passwordResetTokenEntity.setToken(token);
        } else {
            passwordResetTokenEntity.setToken(token);
            passwordResetTokenEntity.setUserEntity(userEntity);
        }

        try {
            passwordResetTokenRepository.save(passwordResetTokenEntity);
            emailSender.sendPasswordResetEmail(userEntity, passwordResetTokenEntity);
            returnValue = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        if (Utils.hasTokenExpired(token)) {
            return false;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            return false;
        }

        //Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        //Update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserEntity();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Verify if password was saved successfully
        if (savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }

        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }
}
