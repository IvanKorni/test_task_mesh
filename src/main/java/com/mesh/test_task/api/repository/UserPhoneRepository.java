package com.mesh.test_task.api.repository;

import com.mesh.test_task.api.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPhoneRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);
    Optional<PhoneData> findByIdAndUser_Id(Long phoneId, Long userId);
}
