package com.learningplatform.webapp.security.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -4890117004486260062L;

    @Id
    @GeneratedValue(generator = "inc")
    @GenericGenerator(name = "inc", strategy = "increment")
    private Long id;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false, length = 120, unique = true)
    private String email;
    @Column(nullable = false)
    private String encryptedPassword;
    private String emailVerificationToken;
    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;

    @OneToOne(mappedBy = "userEntity")
    private PasswordResetTokenEntity passwordResetTokenEntity;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "roles_id",referencedColumnName = "id"))
    private Collection<RoleEntity> roles;
}
