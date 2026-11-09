package com.procuredox.login.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailTaken(String email) {
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    public boolean phoneTaken(String phone) {
        return userRepository.existsByPhone(phone.trim());
    }

    public void register(SignupForm form) {
        AppUser user = new AppUser();
        user.setFirstName(form.firstName().trim());
        user.setLastName(form.lastName().trim());
        user.setEmail(form.email().trim().toLowerCase());
        user.setPhone(form.phone().trim());
        user.setPasswordHash(passwordEncoder.encode(form.password()));
        user.setEnabled(true);
        userRepository.save(user);
    }
}