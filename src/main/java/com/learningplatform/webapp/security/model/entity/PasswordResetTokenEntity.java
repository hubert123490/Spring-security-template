package com.learningplatform.webapp.security.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -2904254733662477411L;

    @Id
    @GeneratedValue(generator = "inc")
    @GenericGenerator(name = "inc", strategy = "increment")
    private long id;
    private String token;
    @OneToOne()
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

}
