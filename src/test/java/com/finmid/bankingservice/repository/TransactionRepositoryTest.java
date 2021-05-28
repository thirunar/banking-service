package com.finmid.bankingservice.repository;

import com.finmid.bankingservice.ContainerInitializer;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
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
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository repository;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = repository.save(Account.builder().build());
        toAccount = repository.save(Account.builder().balance(new BigDecimal("5000.00")).build());
    }

    @Test
    void shouldSaveATransaction() {
        Transaction transaction = Transaction.builder().amount(new BigDecimal("100")).fromAccount(fromAccount)
                .toAccount(toAccount).build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction.getTxnId()).isNotNull();
        assertThat(savedTransaction.getCreateOn()).isNotNull();
    }
}