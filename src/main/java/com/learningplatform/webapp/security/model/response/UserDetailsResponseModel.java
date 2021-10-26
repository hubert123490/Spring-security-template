package com.learningplatform.webapp.security.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponseModel {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
}
