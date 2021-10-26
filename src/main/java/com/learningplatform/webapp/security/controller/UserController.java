package com.learningplatform.webapp.security.controller;

import com.learningplatform.webapp.security.logic.service.UserService;
import com.learningplatform.webapp.security.model.dto.UserDto;
import com.learningplatform.webapp.security.model.request.*;
import com.learningplatform.webapp.security.model.response.OperationStatusModel;
import com.learningplatform.webapp.security.model.response.RequestOperationStatus;
import com.learningplatform.webapp.security.model.response.UserDetailsResponseModel;
import com.learningplatform.webapp.security.shared.Roles;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userId")
    @GetMapping(path = "/{id}")
    public UserDetailsResponseModel getUser(@PathVariable String id) {
        UserDto userDto = userService.getUserByUserId(id);

        return modelMapper.map(userDto, UserDetailsResponseModel.class);
    }

    @PostMapping
    public UserDetailsResponseModel createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        userDto.setRoles(new HashSet<>(Collections.singletonList(Roles.ROLE_USER.name())));

        UserDto createdUser = userService.createUser(userDto);

        return modelMapper.map(createdUser, UserDetailsResponseModel.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    @PutMapping(path = "/{id}")
    public UserDetailsResponseModel updateUser(@PathVariable String id, @RequestBody UserDetailsUpdateRequestModel userDetails) {
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto updatedUser = userService.updateUser(id, userDto);
        return modelMapper.map(updatedUser, UserDetailsResponseModel.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserDetailsResponseModel> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserDetailsResponseModel> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);

        for(UserDto userDto : users) {
            returnValue.add(modelMapper.map(userDto, UserDetailsResponseModel.class));
        }

        return returnValue;
    }

    @GetMapping(path = "/email-verification")
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if(isVerified){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset-request")
    public OperationStatusModel requestPasswordReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());

        if(operationResult){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset")
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
