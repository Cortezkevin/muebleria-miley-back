package com.furniture.miley.model;

import com.furniture.miley.security.enums.RolName;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class MainUser implements UserDetails {
    /*private String firstName;
    private String lastName;*/
    private String username;
    private String email;
    private String password;
    private Set<RolName> roles;

    public static MainUser build(User user){
        return new MainUser(
                /*user.getPersonalInformation().getFirstName(),
                user.getPersonalInformation().getLastName(),*/
                user.getEmail(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map( Role::getRolName ).collect(Collectors.toSet())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(RolName::name).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
