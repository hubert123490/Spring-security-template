package com.learningplatform.webapp.security.exceptions;

public class UserServiceException extends RuntimeException{
    public static long serialVersionUID = -7199859506620176537L;

    public UserServiceException(String message){
        super(message);
    }
}
