package com.mesh.test_task.api.repository;

import com.mesh.test_task.api.entity.Account;
import com.mesh.test_task.api.entity.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface UserAccountRepository extends JpaRepository<Account, Long> {

    Account getAccountByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a join fetch a.user where a.user.id in :userIds order by a.user.id")
    List<Account> findAllByUserIdsForUpdate(@Param("userIds") List<Long> userIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a order by a.id")
    List<Account> findAllForUpdate();
}
