package com.finmid.bankingservice.service;

import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.entity.Transaction;
import com.finmid.bankingservice.exceptions.BalanceNotUpdatedException;
import com.finmid.bankingservice.exceptions.InsufficientBalanceException;
import com.finmid.bankingservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService service;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository repository;

    private final TransactionDto transactionRequest = TransactionDto.builder()
            .fromAccountId(UUID.randomUUID())
            .toAccountId(UUID.randomUUID())
            .amount(new BigDecimal("100"))
            .build();

    private Account fromAccount = Account.builder()
            .id(transactionRequest.getFromAccountId())
            .balance(new BigDecimal("400.00")).build();

    private Account toAccount = Account.builder()
            .id(transactionRequest.getToAccountId())
            .balance(new BigDecimal("600.00")).build();

    @Test
    void shouldSaveTheTransactionAndInvokeAccountServiceToUpdateBalance() {
        when(accountService.debit(any(), any())).thenReturn(fromAccount);
        when(accountService.credit(any(), any())).thenReturn(toAccount);
        when(repository.save(any())).thenReturn(Transaction.builder().txnId(UUID.randomUUID())
                .amount(transactionRequest.getAmount()).fromAccount(fromAccount).toAccount(toAccount).build());

        TransactionDto transaction = service.transfer(transactionRequest);

        verify(accountService).debit(transactionRequest.getFromAccountId(), new BigDecimal("100"));
        verify(accountService).credit(transactionRequest.getToAccountId(), new BigDecimal("100"));

        verify(repository).save(argThat(t -> {
            assertThat(t.getFromAccount()).isEqualTo(fromAccount);
            assertThat(t.getToAccount()).isEqualTo(toAccount);
            return true;
        }));
        assertThat(transaction).usingRecursiveComparison()
                .ignoringFields("txnId")
                .isEqualTo(transactionRequest);
    }

    @Test
    void shouldNotSaveTransactionOnInsufficientBalance() {
        doThrow(InsufficientBalanceException.class).when(accountService).debit(any(), any());

        assertThatExceptionOfType(InsufficientBalanceException.class)
                .isThrownBy(() -> service.transfer(transactionRequest));

        verify(accountService).debit(transactionRequest.getFromAccountId(), new BigDecimal("100"));
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowBalanceNotUpdatedExceptionWhenMaxRetriesExceeds() {
        when(accountService.debit(any(), any())).thenReturn(fromAccount);
        when(accountService.credit(any(), any()))
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class)
                .thenThrow(ObjectOptimisticLockingFailureException.class);

        assertThatExceptionOfType(BalanceNotUpdatedException.class)
                .isThrownBy(() -> service.transfer(transactionRequest));

        verifyNoInteractions(repository);
    }
}