package com.learningplatform.webapp.security.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsUpdateRequestModel {
    private String firstName;
    private String lastName;
}
