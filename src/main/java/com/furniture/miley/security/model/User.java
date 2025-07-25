package com.furniture.miley.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.furniture.miley.delivery.model.Carrier;
import com.furniture.miley.profile.model.Notification;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.security.enums.ResourceStatus;
import com.furniture.miley.security.enums.UserStatus;
import com.furniture.miley.warehouse.model.Grocer;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @JsonIgnore
    private String clientId;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String tokenPassword;
    @JsonIgnore
    private String notificationMobileToken;
    @JsonIgnore
    private String notificationWebToken;

    @Enumerated( EnumType.STRING )
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private ResourceStatus resourceStatus = ResourceStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn( name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn( name = "role_id", nullable = false)
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_notifications",
            joinColumns = @JoinColumn( name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn( name = "notification_id", nullable = false)
    )
    private Set<Notification> notifications = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PersonalInformation personalInformation;

    @OneToOne(mappedBy = "user")
    private Carrier carrier;

    @OneToOne(mappedBy = "user")
    private Grocer grocer;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user")
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}
