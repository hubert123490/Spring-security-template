package com.learningplatform.webapp.security.mail;

import com.learningplatform.webapp.security.constants.SecurityConstants;
import com.learningplatform.webapp.security.model.entity.PasswordResetTokenEntity;
import com.learningplatform.webapp.security.model.entity.UserEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailSender {
    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(UserEntity user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "hubert1234.94@gmail.com";
        String senderName = "Hubex";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Hubex best corpo";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "users/email-verification?token=" + user.getEmailVerificationToken();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(UserEntity user, PasswordResetTokenEntity passwordResetTokenEntity)
            throws MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "hubert1234.94@gmail.com";
        String senderName = "Hubex";
        String subject = "Password reset";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Hubex best corpo";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = SecurityConstants.FRONTEND_RESET_PASSWORD_SITE_URL + "?token=" + passwordResetTokenEntity.getToken();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
}
