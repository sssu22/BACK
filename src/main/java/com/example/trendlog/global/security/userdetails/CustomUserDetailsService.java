package com.example.trendlog.global.security.userdetails;

import com.example.trendlog.domain.User;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.ErrorCode;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return new UserDetailsImpl(user);
    }
}
