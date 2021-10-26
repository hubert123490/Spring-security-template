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
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
public class AuthorityEntity implements Serializable {
    public AuthorityEntity(String name){
        this.name = name;
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 5237410882817898981L;

    @Id
    @GeneratedValue(generator = "inc")
    @GenericGenerator(name = "inc", strategy = "increment")
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Collection<RoleEntity> roles;
}
