package com.finmid.bankingservice.service;

import com.finmid.bankingservice.exceptions.AccountNotFoundException;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.exceptions.InsufficientBalanceException;
import com.finmid.bankingservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository repository;

    @Test
    void shouldCreateAnAccount() {
        Account account = Account.builder().build();
        when(repository.save(any())).thenReturn(account);

        accountService.createAccount(account);

        verify(repository).save(account);
    }

    @Test
    void shouldReturnAccountBasedOnAccountId() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder()
                .id(id).balance(new BigDecimal("100000.00"))
                .build();
        when(repository.findById(any())).thenReturn(Optional.of(account));

        Account actualAccount = accountService.getAccount(id);

        verify(repository).findById(id);
        assertThat(actualAccount).isEqualTo(account);
    }

    @Test
    void shouldThrowAnExceptionWhenTheAccountIsNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        UUID id = UUID.randomUUID();

        assertThatExceptionOfType(AccountNotFoundException.class)
                .isThrownBy(() -> accountService.getAccount(id));

        verify(repository).findById(id);
    }

    @Test
    void shouldDebitFromAccount() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder()
                .id(id).balance(new BigDecimal("100000.00"))
                .build();
        when(repository.findById(any())).thenReturn(Optional.of(account));

        Account debitAccount = accountService.debit(id, new BigDecimal("10"));

        assertThat(debitAccount.getBalance()).isEqualTo(new BigDecimal("99990.00"));
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenTheBalanceIsLessThanTheDebitAmount() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder()
                .id(id).balance(new BigDecimal("100.00"))
                .build();
        when(repository.findById(any())).thenReturn(Optional.of(account));

        assertThatExceptionOfType(InsufficientBalanceException.class)
                .isThrownBy(() -> accountService.debit(id, new BigDecimal("101")));


    }

    @Test
    void shouldCreditFromAccount() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder()
                .id(id).balance(new BigDecimal("100000.00"))
                .build();
        when(repository.findById(any())).thenReturn(Optional.of(account));

        Account creditAccount = accountService.credit(id, new BigDecimal("10"));

        assertThat(creditAccount.getBalance()).isEqualTo(new BigDecimal("100010.00"));
    }
}