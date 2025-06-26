package com.furniture.miley.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.furniture.miley.model.Cart;
import com.furniture.miley.model.PersonalInformation;
import com.furniture.miley.security.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String tokenPassword;

    @Enumerated( EnumType.STRING )
    private Status status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn( name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn( name = "role_id", nullable = false)
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToOne(mappedBy = "user")
    private PersonalInformation personalInformation;

    /*@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Order> orders = new ArrayList<>();*/
}
