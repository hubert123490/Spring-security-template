package com.learningplatform.webapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningplatform.webapp.security.constants.SecurityConstants;
import com.learningplatform.webapp.security.logic.service.UserService;
import com.learningplatform.webapp.security.model.dto.UserDto;
import com.learningplatform.webapp.security.model.request.UserLoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            UserLoginRequestModel credentials = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getEmail(),
                            credentials.getPassword(),
                            new ArrayList<>())
                    );
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult){
        String userName = ((UserPrincipal) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.addHeader("UserID", userDto.getUserId());
    }
}
