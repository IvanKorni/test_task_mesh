package com.mesh.test_task.api.repository;

import com.mesh.test_task.api.entity.Account;
import com.mesh.test_task.api.entity.EmailData;
import com.mesh.test_task.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<Account, Long> {

    Account getAccountByUser(User user);
}
