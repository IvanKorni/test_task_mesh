package com.mesh.test_task.api.service;

import com.mesh.test_task.api.entity.Account;
import com.mesh.test_task.api.exception.ApiException;
import com.mesh.test_task.api.generated.model.TransferRequest;
import com.mesh.test_task.api.generated.model.TransferResponse;
import com.mesh.test_task.api.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public TransferResponse transfer(Long fromUserId, TransferRequest request) {
        Long toUserId = request.getToUserId();
        BigDecimal amount = validateAmount(request.getAmount());
        validateUsers(fromUserId, toUserId);

        Map<Long, Account> accountsByUserId = userAccountRepository
                .findAllByUserIdsForUpdate(Arrays.asList(fromUserId, toUserId))
                .stream()
                .collect(Collectors.toMap(account -> account.getUser().getId(), Function.identity()));

        Account fromAccount = account(accountsByUserId, fromUserId);
        Account toAccount = account(accountsByUserId, toUserId);
        validateEnoughMoney(fromAccount, amount);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        return response(fromUserId, toUserId, amount, fromAccount, toAccount);
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transfer amount must be positive");
        }
        if (amount.scale() > 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transfer amount must have no more than 2 decimal places");
        }
        return amount.setScale(2);
    }

    private void validateUsers(Long fromUserId, Long toUserId) {
        if (toUserId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Receiver user id is required");
        }
        if (fromUserId.equals(toUserId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot transfer money to yourself");
        }
    }

    private Account account(Map<Long, Account> accountsByUserId, Long userId) {
        Account account = accountsByUserId.get(userId);
        if (account == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Account not found for user " + userId);
        }
        return account;
    }

    private void validateEnoughMoney(Account fromAccount, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.warn("Money transfer rejected: fromUserId={}, amount={}", fromAccount.getUser().getId(), amount);
            throw new ApiException(HttpStatus.BAD_REQUEST, "Not enough money");
        }
    }

    private TransferResponse response(
            Long fromUserId,
            Long toUserId,
            BigDecimal amount,
            Account fromAccount,
            Account toAccount
    ) {
        TransferResponse response = new TransferResponse();
        response.setFromUserId(fromUserId);
        response.setToUserId(toUserId);
        response.setAmount(amount);
        response.setFromBalance(fromAccount.getBalance());
        response.setToBalance(toAccount.getBalance());
        return response;
    }
}
