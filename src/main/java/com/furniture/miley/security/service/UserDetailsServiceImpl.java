package com.furniture.miley.security.service;

import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail( email ).orElseThrow(() -> new UsernameNotFoundException("Email ingreso no existe"));
        return MainUser.build( user );
    }

}
