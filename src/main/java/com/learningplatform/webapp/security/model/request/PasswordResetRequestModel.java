package com.learningplatform.webapp.security.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestModel {
    private String email;
}
