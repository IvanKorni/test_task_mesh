package com.mesh.test_task.api.rest;

import com.mesh.test_task.api.generated.UserInfoApi;
import com.mesh.test_task.api.generated.model.*;
import com.mesh.test_task.api.service.UserContactService;
import com.mesh.test_task.api.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@RequiredArgsConstructor
public class CurrentUserInfoController implements UserInfoApi {
    private final UserContactService userContactService;
    private final UserSearchService userSearchService;

    @Override
    public ResponseEntity<UserEmailResponse> addEmail(@RequestBody AddEmailRequest request) {
        UserEmailResponse response = userContactService.addEmail(currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<UserEmailResponse> updateEmail(@PathVariable Long emailId, @RequestBody UpdateEmailRequest request) {
        UserEmailResponse response = userContactService.updateEmail(currentUserId(), emailId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteEmail(@PathVariable Long emailId) {
        userContactService.deleteEmail(currentUserId(), emailId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserPhoneResponse> addPhone(@RequestBody AddPhoneRequest request) {
        UserPhoneResponse response = userContactService.addPhone(currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<UserPhoneResponse> updatePhone(@PathVariable Long phoneId, @RequestBody UpdatePhoneRequest request) {
        UserPhoneResponse response = userContactService.updatePhone(currentUserId(), phoneId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deletePhone(@PathVariable Long phoneId) {
        userContactService.deletePhone(currentUserId(), phoneId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserSearchResponse> searchUsers(String phone, String email, String name, Integer page, Integer size) {
        UserSearchResponse response = userSearchService.searchUsers(phone, email, name, page, size);
        return ResponseEntity.ok(response);
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Number)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return ((Number) authentication.getPrincipal()).longValue();
    }
}
