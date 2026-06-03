package com.mesh.test_task.api.rest;

import com.mesh.test_task.api.generated.AuthApi;
import com.mesh.test_task.api.generated.model.LoginRequest;
import com.mesh.test_task.api.generated.model.TokenResponse;
import com.mesh.test_task.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
