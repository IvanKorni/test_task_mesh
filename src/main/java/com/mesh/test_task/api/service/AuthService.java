package com.mesh.test_task.api.service;

import com.mesh.test_task.api.exception.ApiException;
import com.mesh.test_task.api.entity.User;
import com.mesh.test_task.api.generated.model.LoginRequest;
import com.mesh.test_task.api.generated.model.TokenResponse;
import com.mesh.test_task.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TokenResponse login(LoginRequest request) {
        User user = findUserByLogin(request.getLogin());

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(jwtService.generateToken(user.getId()));
        return tokenResponse;
    }

    private User findUserByLogin(String login) {
        Optional<User> user = login.contains("@")
                ? userRepository.findByEmail(login)
                : userRepository.findByPhone(login);

        return user.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid login or password"));
    }
}
