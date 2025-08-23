package org.example.sutod_auth.Services.Impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with username '%s'.", username)));
        return UserDetailsImpl.build(user);
    }
}
