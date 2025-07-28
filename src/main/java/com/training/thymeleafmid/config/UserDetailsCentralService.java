package com.training.thymeleafmid.config;

import com.training.thymeleafmid.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsCentralService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsCentralService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        com.training.thymeleafmid.user.User user = userRepository.findById(Long.parseLong(username)).
                orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + username));

        return new User(
                username,
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );

    }
}
