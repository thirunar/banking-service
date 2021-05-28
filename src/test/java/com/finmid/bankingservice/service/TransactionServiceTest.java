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

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService service;

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
        Account fromAccount = Account.builder().id(request.getFromAccountId()).balance(new BigDecimal("500.00")).build();
        Account toAccount = Account.builder().id(request.getToAccountId()).balance(new BigDecimal("500.00")).build();
        when(accountService.getAccount(any())).thenReturn(fromAccount, toAccount);
        when(repository.save(any())).thenReturn(Transaction.builder().txnId(UUID.randomUUID())
                .amount(request.getAmount()).fromAccount(fromAccount).toAccount(toAccount).build());

        TransactionDto transaction = service.createTransaction(request);

        verify(accountService).getAccount(request.getFromAccountId());
        verify(accountService).getAccount(request.getToAccountId());
        verify(repository).save(argThat(t -> {
            assertThat(t.getFromAccount().getId()).isEqualTo(request.getFromAccountId());
            assertThat(t.getFromAccount().getBalance()).isEqualTo(new BigDecimal("400.00"));
            assertThat(t.getToAccount().getId()).isEqualTo(request.getToAccountId());
            assertThat(t.getToAccount().getBalance()).isEqualTo(new BigDecimal("600.00"));
            assertThat(t.getAmount()).isEqualTo(request.getAmount());
            return true;
        }));
        assertThat(transaction).usingRecursiveComparison()
                .ignoringFields("txnId")
                .isEqualTo(request);
    }
}