package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Role;
import com.spoffy.musiccloud.domain.User;
import com.spoffy.musiccloud.dto.auth.AuthResponse;
import com.spoffy.musiccloud.dto.auth.LoginRequest;
import com.spoffy.musiccloud.dto.auth.RegisterRequest;
import com.spoffy.musiccloud.dto.user.UserResponse;
import com.spoffy.musiccloud.exception.DuplicateResourceException;
import com.spoffy.musiccloud.exception.ResourceNotFoundException;
import com.spoffy.musiccloud.repository.UserRepository;
import com.spoffy.musiccloud.security.JwtService;
import com.spoffy.musiccloud.security.UserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setDisplayName(request.displayName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(UserPrincipal.from(saved));
        return new AuthResponse(saved.getId(), saved.getEmail(), saved.getDisplayName(), saved.getRole().name(), token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmailIgnoreCase(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = jwtService.generateToken(principal);
        return new AuthResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name(), token);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name(), user.getCreatedAt()))
                .toList();
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}