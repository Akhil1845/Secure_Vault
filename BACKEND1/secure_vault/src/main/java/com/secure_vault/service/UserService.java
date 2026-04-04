package com.secure_vault.service;

import org.springframework.stereotype.Service;
import com.secure_vault.entity.User;
import com.secure_vault.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String identifier, String password) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(identifier, identifier);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User registerUser(String username, String email, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("This username is already taken");
        }
        if (email != null && !email.isBlank() && userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // In a real app we would encode this password!
        newUser.setRole(role);
        return userRepository.save(newUser);
    }

}
