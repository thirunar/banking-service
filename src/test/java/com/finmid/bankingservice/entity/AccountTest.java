package com.finmid.bankingservice.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void shouldAddAmountToTheBalance() {
        Account account = Account.builder().balance(new BigDecimal("100")).build();

        account.credit(new BigDecimal("20"));

        assertThat(account.getBalance()).isEqualTo(new BigDecimal("120"));
    }

    @Test
    void shouldSubtractAmountToTheBalance() {
        Account account = Account.builder().balance(new BigDecimal("100")).build();

        account.debit(new BigDecimal("20"));

        assertThat(account.getBalance()).isEqualTo(new BigDecimal("80"));
    }
}