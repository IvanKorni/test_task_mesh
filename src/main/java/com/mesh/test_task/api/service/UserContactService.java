package com.mesh.test_task.api.service;

import com.mesh.test_task.api.entity.EmailData;
import com.mesh.test_task.api.entity.PhoneData;
import com.mesh.test_task.api.entity.User;
import com.mesh.test_task.api.generated.model.*;
import com.mesh.test_task.api.mapper.UserContactMapper;
import com.mesh.test_task.api.repository.UserEmailRepository;
import com.mesh.test_task.api.repository.UserPhoneRepository;
import com.mesh.test_task.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public UserEmailResponse addEmail(Long userId, AddEmailRequest request) {
        if (userEmailRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmailData email = userContactMapper.toEmailData(request);
        email.setUser(user);

        EmailData entity =  userEmailRepository.save(email);
        return userContactMapper.toEmailResponse(entity);
    }

    @Transactional
    public UserEmailResponse updateEmail(Long userId, Long emailId, UpdateEmailRequest request) {
        EmailData email = userEmailRepository.findByIdAndUser_Id(emailId, userId)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!email.getEmail().equals(request.getEmail()) && userEmailRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        email.setEmail(request.getEmail());

        EmailData entity = userEmailRepository.save(email);
        return  userContactMapper.toEmailResponse(entity);
    }

    @Transactional
    public void deleteEmail(Long userId, Long emailId) {
        EmailData email = userEmailRepository.findByIdAndUser_Id(emailId, userId)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        long count = userEmailRepository.countByUser_Id(userId);
        if (count <= 1) throw new RuntimeException("Can't delete last email");
        userEmailRepository.delete(email);
    }

    @Transactional
    public UserPhoneResponse addPhone(Long userId, AddPhoneRequest request) {
        if (userPhoneRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PhoneData phone = userContactMapper.toPhoneData(request);
        phone.setUser(user);

        PhoneData entity =  userPhoneRepository.save(phone);
        return userContactMapper.toPhoneResponse(entity);
    }

    @Transactional
    public UserPhoneResponse updatePhone(Long userId, Long phoneId, UpdatePhoneRequest request) {
        PhoneData phone = userPhoneRepository.findByIdAndUser_Id(phoneId, userId)
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        if (!phone.getPhone().equals(request.getPhone()) && userPhoneRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        phone.setPhone(request.getPhone());

        PhoneData entity = userPhoneRepository.save(phone);
        return  userContactMapper.toPhoneResponse(entity);
    }

    @Transactional
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData phone = userPhoneRepository.findByIdAndUser_Id(phoneId, userId)
                .orElseThrow(() -> new RuntimeException("Phone not found"));
        userPhoneRepository.delete(phone);
    }
}
