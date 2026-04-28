package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.AuthResponseDto;
import com.csis231.springpostgrescrud.dto.LoginDto;
import com.csis231.springpostgrescrud.dto.UserDto;
import com.csis231.springpostgrescrud.entity.User;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.UserMapper;
import com.csis231.springpostgrescrud.repository.UserRepository;
import com.csis231.springpostgrescrud.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto registerUser(UserDto userDto) {
        validateRegistration(userDto);

        if (userRepository.existsByUsernameIgnoreCase(userDto.getUsername().trim())) {
            throw new BadRequestException("Username is already taken.");
        }
        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail().trim())) {
            throw new BadRequestException("Email is already registered.");
        }

        User user = UserMapper.toEntity(userDto);
        user.setUsername(userDto.getUsername().trim());
        user.setEmail(userDto.getEmail().trim());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole());
        return new AuthResponseDto(token, UserMapper.toDto(savedUser));
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given id: " + id));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> searchUsers(String q, Pageable pageable) {
        String query = q == null ? "" : q.trim();
        if (query.isEmpty()) {
            return userRepository.findAll(pageable).map(UserMapper::toDto);
        }
        return userRepository
                .dynamicSearch(query, pageable)
                .map(UserMapper::toDto);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given id: " + id));

        String username = normalize(userDto.getUsername());
        String email = normalize(userDto.getEmail());

        if (username == null) {
            throw new BadRequestException("Username is required.");
        }
        if (email == null) {
            throw new BadRequestException("Email is required.");
        }

        if (!user.getUsername().equalsIgnoreCase(username) && userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username is already taken.");
        }
        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email is already registered.");
        }

        user.setUsername(username);
        user.setEmail(email);
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(hashPassword(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with given id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public AuthResponseDto authenticateUser(LoginDto loginDto) {
        if (loginDto == null) {
            throw new BadRequestException("Username and password are required.");
        }
        String identifier = normalize(loginDto.getUsername());
        String password = loginDto.getPassword();

        if (identifier == null || password == null || password.isBlank()) {
            throw new BadRequestException("Username and password are required.");
        }

        User user = userRepository.findByUsernameIgnoreCase(identifier)
                .or(() -> userRepository.findByEmailIgnoreCase(identifier))
                .orElseThrow(() -> new BadRequestException("Invalid username or password."));

        if (!passwordMatches(password, user.getPassword())) {
            throw new BadRequestException("Invalid username or password.");
        }

        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }
        String token = jwtService.generateToken(user.getUsername(), role);
        return new AuthResponseDto(token, UserMapper.toDto(user));
    }

    @Override
    public UserDto getCurrentUser(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("User not found.");
        }
        User user = userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new BadRequestException("User not found."));
        return UserMapper.toDto(user);
    }

    private void validateRegistration(UserDto userDto) {
        if (userDto == null) {
            throw new BadRequestException("Registration details are required.");
        }
        if (normalize(userDto.getUsername()) == null) {
            throw new BadRequestException("Username is required.");
        }
        if (normalize(userDto.getEmail()) == null) {
            throw new BadRequestException("Email is required.");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new BadRequestException("Password is required.");
        }
        if (userDto.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long.");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }
        if (!storedPassword.startsWith("sha256:")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return hashPassword(rawPassword).equals(storedPassword);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return "sha256:" + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 hashing is not available.", e);
        }
    }
}
