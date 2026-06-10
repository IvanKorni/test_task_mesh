package com.mesh.test_task.api.service;

import com.mesh.test_task.api.entity.Account;
import com.mesh.test_task.api.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "balance.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class BalanceService {
    private final UserAccountRepository accountRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void increaseBalance() {
        List<Account> accounts = accountRepository.findAllForUpdate();
        for (var account : accounts) {
            BigDecimal startBalance = account.getStartBalance();
            BigDecimal balance = account.getBalance();
            BigDecimal cap = startBalance.multiply(new BigDecimal("2.07"));
            if (balance.compareTo(cap) < 0) {
                BigDecimal increased = balance.multiply(new BigDecimal("1.10"));
                BigDecimal newBalance = increased.min(cap).setScale(2, RoundingMode.HALF_UP);
                account.setBalance(newBalance);
            }
        }
    }
}
