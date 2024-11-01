package com.taltech.alpopo.securepasswordmanager.service;

import com.taltech.alpopo.securepasswordmanager.entity.User;
import com.taltech.alpopo.securepasswordmanager.exception.DuplicateResourceException;
import com.taltech.alpopo.securepasswordmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String rawPassword, String email, String rawMasterPassword) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new DuplicateResourceException("User with username %s already exists.", username);
        }

        Optional<User> existingEmail = userRepository.findByEmail(email);
        if (existingEmail.isPresent()) {
            throw new DuplicateResourceException("Email %s is already in use.", email);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        String encodedMasterPassword = passwordEncoder.encode(rawMasterPassword);

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .masterPassword(encodedMasterPassword)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validateMasterPassword(User user, String rawMasterPassword) {
        return passwordEncoder.matches(rawMasterPassword, user.getMasterPassword());
    }
}
