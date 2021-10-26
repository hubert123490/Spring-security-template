package com.learningplatform.webapp.security.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity implements Serializable {
    public RoleEntity(String name){
        this.name = name;
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 8969720032605789160L;

    @Id
    @GeneratedValue(generator = "inc")
    @GenericGenerator(name = "inc", strategy = "increment")
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> users;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "roles_authorities",
            joinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authorities_id",referencedColumnName = "id"))
    private Collection<AuthorityEntity> authorities;
}
