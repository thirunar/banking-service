package com.finmid.bankingservice.repository;

import com.finmid.bankingservice.ContainerInitializer;
import com.finmid.bankingservice.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(initializers = ContainerInitializer.class)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void shouldStoreAccountWithPredefinedBalance() {

        Account savedAccount = repository.save(Account.builder().build());

        assertThat(savedAccount.getBalance()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getCreateOn()).isNotNull();
        assertThat(savedAccount.getLastModifiedOn()).isNull();
    }
}