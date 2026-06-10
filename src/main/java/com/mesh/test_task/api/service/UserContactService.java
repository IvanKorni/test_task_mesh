package com.mesh.test_task.api.service;

import com.mesh.test_task.api.entity.EmailData;
import com.mesh.test_task.api.entity.PhoneData;
import com.mesh.test_task.api.entity.User;
import com.mesh.test_task.api.config.CacheConfig;
import com.mesh.test_task.api.exception.ApiException;
import com.mesh.test_task.api.generated.model.AddEmailRequest;
import com.mesh.test_task.api.generated.model.AddPhoneRequest;
import com.mesh.test_task.api.generated.model.UpdateEmailRequest;
import com.mesh.test_task.api.generated.model.UpdatePhoneRequest;
import com.mesh.test_task.api.generated.model.UserEmailResponse;
import com.mesh.test_task.api.generated.model.UserPhoneResponse;
import com.mesh.test_task.api.mapper.UserContactMapper;
import com.mesh.test_task.api.repository.UserEmailRepository;
import com.mesh.test_task.api.repository.UserPhoneRepository;
import com.mesh.test_task.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserContactService {
    private final UserEmailRepository userEmailRepository;
    private final UserRepository userRepository;
    private final UserContactMapper userContactMapper;
    private final UserPhoneRepository userPhoneRepository;

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.USER_SEARCH_CACHE, allEntries = true)
    public UserEmailResponse addEmail(Long userId, AddEmailRequest request) {
        if (userEmailRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        EmailData email = userContactMapper.toEmailData(request);
        email.setUser(user);

        EmailData entity = userEmailRepository.save(email);
        log.info("Email added: userId={}, emailId={}", userId, entity.getId());
        return userContactMapper.toEmailResponse(entity);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.USER_SEARCH_CACHE, allEntries = true)
    public UserEmailResponse updateEmail(Long userId, Long emailId, UpdateEmailRequest request) {
        EmailData email = userEmailRepository.findByIdAndUser_Id(emailId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Email not found"));

        if (!email.getEmail().equals(request.getEmail()) && userEmailRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        email.setEmail(request.getEmail());

        EmailData entity = userEmailRepository.save(email);
        log.info("Email updated: userId={}, emailId={}", userId, entity.getId());
        return userContactMapper.toEmailResponse(entity);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.USER_SEARCH_CACHE, allEntries = true)
    public void deleteEmail(Long userId, Long emailId) {
        EmailData email = userEmailRepository.findByIdAndUser_Id(emailId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Email not found"));

        long count = userEmailRepository.countByUser_Id(userId);
        if (count <= 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Can't delete last email");
        }
        userEmailRepository.delete(email);
        log.info("Email deleted: userId={}, emailId={}", userId, emailId);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.USER_SEARCH_CACHE, allEntries = true)
    public UserPhoneResponse addPhone(Long userId, AddPhoneRequest request) {
        if (userPhoneRepository.existsByPhone(request.getPhone())) {
            throw new ApiException(HttpStatus.CONFLICT, "Phone already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        PhoneData phone = userContactMapper.toPhoneData(request);
        phone.setUser(user);

        PhoneData entity = userPhoneRepository.save(phone);
        log.info("Phone added: userId={}, phoneId={}", userId, entity.getId());
        return userContactMapper.toPhoneResponse(entity);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.USER_SEARCH_CACHE, allEntries = true)
    public UserPhoneResponse updatePhone(Long userId, Long phoneId, UpdatePhoneRequest request) {
        PhoneData phone = userPhoneRepository.findByIdAndUser_Id(phoneId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Phone not found"));

        if (!phone.getPhone().equals(request.getPhone()) && userPhoneRepository.existsByPhone(request.getPhone())) {
            throw new ApiException(HttpStatus.CONFLICT, "Phone already exists");
        }

        phone.setPhone(request.getPhone());

        PhoneData entity = userPhoneRepository.save(phone);
        log.info("Phone updated: userId={}, phoneId={}", userId, entity.getId());
        return userContactMapper.toPhoneResponse(entity);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.USER_SEARCH_CACHE, allEntries = true)
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData phone = userPhoneRepository.findByIdAndUser_Id(phoneId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Phone not found"));

        long count = userPhoneRepository.countByUser_Id(userId);
        if (count <= 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Can't delete last phone");
        }
        userPhoneRepository.delete(phone);
        log.info("Phone deleted: userId={}, phoneId={}", userId, phoneId);
    }
}
