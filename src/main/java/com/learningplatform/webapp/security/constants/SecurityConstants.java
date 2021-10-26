package com.learningplatform.webapp.security.constants;

import com.learningplatform.webapp.security.SpringApplicationContext;
import com.learningplatform.webapp.security.properties.AppProperties;

public class SecurityConstants {
    public static final long TOKEN_EXPIRATION_TIME = 1000*60*60*24*7; // 7 days
    public static final long PASSWORD_RESET_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String SITE_URL = "http://localhost:8080/";
    public static final String FRONTEND_RESET_PASSWORD_SITE_URL = "http://localhost:8080/tmp";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
