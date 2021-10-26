package com.learningplatform.webapp.security.logic.serviceImpl;

import com.learningplatform.webapp.security.mail.EmailSender;
import com.learningplatform.webapp.security.model.dto.UserDto;
import com.learningplatform.webapp.security.model.entity.UserEntity;
import com.learningplatform.webapp.security.model.repository.UserRepository;
import com.learningplatform.webapp.security.shared.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    EmailSender emailSender;

    private final ModelMapper modelMapper = new ModelMapper();


    Long id = 1L;
    String userId = "asf23";
    String firstName = "John";
    String lastName = "Connor";
    String email = "johnny@mail.com";
    String encryptedPassword = "asdf234fdh13we";
    String emailVerificationToken = "sfdg43rfsg";

    UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setUserId(userId);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmailVerificationToken(emailVerificationToken);
    }

    @Test
    void createUser() throws MessagingException, UnsupportedEncodingException {
        //given
        UserDto userDto = new UserDto();
        userDto.setFirstName("sdfg");
        userDto.setLastName("vbcncbvn");
        userDto.setEmail("mail@to.pl");
        userDto.setPassword("123");
        //expected
        UserEntity expected = modelMapper.map(userDto, UserEntity.class);
        String userId = "fdghg324fd";
        String emailVerificationToken = "2342345fdsgdfh234";
        String encryptedPassword = "sfdg234bhdgbdg234";
        //when
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(utils.generateEmailVerificationToken(String.valueOf(anyInt()))).thenReturn(emailVerificationToken);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(expected);
        Mockito.doNothing().when(emailSender).sendVerificationEmail(any(UserEntity.class), anyString());
        //then
        UserDto returnValue = userService.createUser(userDto);

        Assertions.assertNotNull(returnValue);
        Assertions.assertEquals(expected.getFirstName(), returnValue.getFirstName());
        Assertions.assertEquals(expected.getLastName(), returnValue.getLastName());
        Assertions.assertEquals(expected.getEmail(), returnValue.getEmail());
        Assertions.assertEquals(expected.getUserId(), returnValue.getUserId());
        Assertions.assertEquals(expected.getEncryptedPassword(), returnValue.getEncryptedPassword());
        Assertions.assertEquals(expected.getEmailVerificationStatus(), false);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(utils, times(1)).generateUserId(anyInt());
        verify(utils, times(1)).generateEmailVerificationToken(anyString());
        verify(bCryptPasswordEncoder, times(1)).encode(userDto.getPassword());
    }

    @Test
    void createUser_throwsRuntimeException() {
        //when
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        //then
        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(new UserDto()));
    }

    @Test
    void getUser_throwsUsernameNotFoundException() {
        //given
        //when
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        //then
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.getUser("anyEmail@com.pl"));
    }

    @Test
    void getUser() {
        //given
        //when
        when(userRepository.findByEmail("test@mail.com")).thenReturn(userEntity);
        UserDto userDto = userService.getUser("test@mail.com");

        //then
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals("John", userDto.getFirstName());
    }

}