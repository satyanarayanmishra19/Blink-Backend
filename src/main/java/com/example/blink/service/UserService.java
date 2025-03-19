package com.example.blink.service;

import com.example.blink.dto.UserRequest;
import com.example.blink.model.User;
import com.example.blink.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> emailUpdateStorage = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService(UserRepository userRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUserName(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        userRepository.save(user);
    }

    public User updateUserDetails(String username, String name, String email, String phone, String countryCode, String password) {
        User user = userRepository.findByUsername(username);
                
        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setCountryCode(countryCode);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void updateUserPreferences(String username, Set<String> preferences) {
        User user = userRepository.findByUsername(username);
        user.setPreferences(preferences);
        userRepository.save(user);
    }
    
    public void updateUserName(String username, String name){
        User user = userRepository.findByUsername(username);
        user.setName(name);
        userRepository.save(user);
    }

    public void generateEmailUpdateOtp(String username, String newEmail) {
        logger.info("generateEmailUpdateOtp called for username: " + username + " and newEmail: " + newEmail);

        User user = userRepository.findByUsername(username);

        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        String otp = generateOtp();
        otpStorage.put(username, otp);
        emailUpdateStorage.put(username, newEmail);

        logger.info("Generated OTP: " + otp);

        sendOtpToEmail(newEmail, otp);
    }

    public void verifyEmailOtp(String username, String otp) {
        String storedOtp = otpStorage.get(username);
        String newEmail = emailUpdateStorage.get(username);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        User user = userRepository.findByUsername(username);

        user.setEmail(newEmail);
        userRepository.save(user);

        otpStorage.remove(username);
        emailUpdateStorage.remove(username);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    private void sendOtpToEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
        logger.info("OTP sent to email: " + email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String searchQuery) {
        return userRepository.findByUsernameContainingOrNameContaining(searchQuery, searchQuery);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean hasMatchingPreferences(String username, User otherUser) {
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null || otherUser == null) {
            return false;
        }
        Set<String> currentUserPreferences = currentUser.getPreferences();
        Set<String> otherUserPreferences = otherUser.getPreferences();
        return currentUserPreferences != null && otherUserPreferences != null &&
               !currentUserPreferences.isEmpty() &&
               currentUserPreferences.stream().anyMatch(otherUserPreferences::contains);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }
}
