package com.finmid.bankingservice.service;

import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.entity.Transaction;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @InjectMocks
    private TransferService service;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository repository;

    @Test
    void shouldSaveTheTransactionWithUpdatedBalance() {
        TransactionDto request = TransactionDto.builder()
                .fromAccountId(UUID.randomUUID())
                .toAccountId(UUID.randomUUID())
                .amount(new BigDecimal("100"))
                .build();
        Account fromAccount = Account.builder().id(request.getFromAccountId()).balance(new BigDecimal("400.00")).build();
        Account toAccount = Account.builder().id(request.getToAccountId()).balance(new BigDecimal("600.00")).build();
        when(accountService.debit(any(), any())).thenReturn(fromAccount);
        when(accountService.credit(any(), any())).thenReturn(toAccount);
        when(repository.save(any())).thenReturn(Transaction.builder().txnId(UUID.randomUUID())
                .amount(request.getAmount()).fromAccount(fromAccount).toAccount(toAccount).build());

        TransactionDto transaction = service.createTransaction(request);

        verify(accountService).debit(request.getFromAccountId(), new BigDecimal("100"));
        verify(accountService).credit(request.getToAccountId(), new BigDecimal("100"));

        verify(repository).save(argThat(t -> {
            assertThat(t.getFromAccount()).isEqualTo(fromAccount);
            assertThat(t.getToAccount()).isEqualTo(toAccount);
            return true;
        }));
        assertThat(transaction).usingRecursiveComparison()
                .ignoringFields("txnId")
                .isEqualTo(request);
    }

    @Test
    void shouldRetryWhenBalanceIsNotUpdatedDueToOptimisticLocking() {
        TransactionDto request = TransactionDto.builder()
                .fromAccountId(UUID.randomUUID())
                .toAccountId(UUID.randomUUID())
                .amount(new BigDecimal("100"))
                .build();
        Account fromAccount = Account.builder().id(request.getFromAccountId()).balance(new BigDecimal("400.00")).build();
        Account toAccount = Account.builder().id(request.getToAccountId()).balance(new BigDecimal("600.00")).build();

        when(accountService.debit(any(), any()))
                .thenThrow(ObjectOptimisticLockingFailureException.class).thenReturn(fromAccount);
        when(accountService.credit(any(), any()))
                .thenThrow(ObjectOptimisticLockingFailureException.class).thenReturn(toAccount);
        when(repository.save(any())).thenReturn(Transaction.builder().txnId(UUID.randomUUID())
                .amount(request.getAmount()).fromAccount(fromAccount).toAccount(toAccount).build());

        service.createTransaction(request);

        verify(accountService, times(2)).debit(request.getFromAccountId(), new BigDecimal("100"));
        verify(accountService, times(2)).credit(request.getToAccountId(), new BigDecimal("100"));
    }
}