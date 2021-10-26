package com.learningplatform.webapp.security.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class UserDto implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -5022382221218084031L;
    private long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private Collection<String> roles;

}
