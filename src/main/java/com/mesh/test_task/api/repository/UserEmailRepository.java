package com.mesh.test_task.api.repository;

import com.mesh.test_task.api.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserEmailRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmail(String email);
    Optional<EmailData> findByIdAndUser_Id(Long emailId, Long userId);
    long countByUser_Id(Long userId);
}
