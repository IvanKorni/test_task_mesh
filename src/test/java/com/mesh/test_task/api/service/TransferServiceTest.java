package com.mesh.test_task.api.service;

import com.mesh.test_task.api.entity.Account;
import com.mesh.test_task.api.entity.User;
import com.mesh.test_task.api.generated.model.TransferRequest;
import com.mesh.test_task.api.generated.model.TransferResponse;
import com.mesh.test_task.api.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transfersMoneyBetweenUsers() {
        Account fromAccount = account(1L, "100.00");
        Account toAccount = account(2L, "50.00");
        when(userAccountRepository.findAllByUserIdsForUpdate(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(fromAccount, toAccount));

        TransferResponse response = transferService.transfer(1L, request(2L, "25.50"));

        assertEquals(new BigDecimal("74.50"), fromAccount.getBalance());
        assertEquals(new BigDecimal("75.50"), toAccount.getBalance());
        assertEquals(new BigDecimal("25.50"), response.getAmount());
        assertEquals(new BigDecimal("74.50"), response.getFromBalance());
        assertEquals(new BigDecimal("75.50"), response.getToBalance());
    }

    @Test
    void rejectsTransferToSelf() {
        TransferRequest request = request(1L, "10.00");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferService.transfer(1L, request)
        );

        assertEquals("Cannot transfer money to yourself", exception.getMessage());
    }

    @Test
    void rejectsNonPositiveAmount() {
        TransferRequest request = request(2L, "0.00");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferService.transfer(1L, request)
        );

        assertEquals("Transfer amount must be positive", exception.getMessage());
    }

    @Test
    void rejectsAmountWithMoreThanTwoDecimalPlaces() {
        TransferRequest request = request(2L, "10.001");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferService.transfer(1L, request)
        );

        assertEquals("Transfer amount must have no more than 2 decimal places", exception.getMessage());
    }

    @Test
    void rejectsTransferWhenBalanceIsInsufficient() {
        Account fromAccount = account(1L, "10.00");
        Account toAccount = account(2L, "50.00");
        when(userAccountRepository.findAllByUserIdsForUpdate(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(fromAccount, toAccount));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferService.transfer(1L, request(2L, "10.01"))
        );

        assertEquals("Not enough money", exception.getMessage());
        assertEquals(new BigDecimal("10.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("50.00"), toAccount.getBalance());
    }

    private TransferRequest request(Long toUserId, String amount) {
        TransferRequest request = new TransferRequest();
        request.setToUserId(toUserId);
        request.setAmount(new BigDecimal(amount));
        return request;
    }

    private Account account(Long userId, String balance) {
        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(new BigDecimal(balance));
        return account;
    }
}
