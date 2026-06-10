package com.mesh.test_task.api.service;

import com.mesh.test_task.api.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "balance.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private static final int BALANCE_UPDATE_BATCH_SIZE = 10_000;

    private final UserAccountRepository accountRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void increaseBalance() {
        int updatedAccounts = accountRepository.increaseBalances(BALANCE_UPDATE_BATCH_SIZE);
        log.debug("Balances increased: updatedAccounts={}", updatedAccounts);
    }
}
