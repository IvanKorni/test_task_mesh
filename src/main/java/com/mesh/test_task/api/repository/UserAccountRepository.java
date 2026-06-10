package com.mesh.test_task.api.repository;

import com.mesh.test_task.api.entity.Account;
import com.mesh.test_task.api.entity.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query(value = "with batch as ("
            + "    select id "
            + "    from account "
            + "    where balance < start_balance * 2.07 "
            + "    order by id "
            + "    limit :batchSize "
            + "    for update skip locked"
            + ") "
            + "update account a "
            + "set balance = round(least(a.balance * 1.10, a.start_balance * 2.07), 2) "
            + "from batch "
            + "where a.id = batch.id",
            nativeQuery = true)
    int increaseBalances(@Param("batchSize") int batchSize);
}
