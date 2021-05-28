package com.finmid.bankingservice;

import com.finmid.bankingservice.ContainerInitializer;
import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.repository.AccountRepository;
import com.finmid.bankingservice.repository.TransactionRepository;
import com.finmid.bankingservice.service.AccountService;
import com.finmid.bankingservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = ContainerInitializer.class)
class BankingServiceITest {

    @Autowired
    private TransactionService service;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = accountRepository.save(Account.builder().balance(new BigDecimal("10000.00")).build());
        toAccount = accountRepository.save(Account.builder().balance(new BigDecimal("5000.00")).build());
    }

    @Test
    void shouldSaveTheTransactionWithUpdatedBalance() {
        TransactionDto request = TransactionDto.builder()
                .fromAccountId(fromAccount.getId())
                .toAccountId(toAccount.getId())
                .amount(new BigDecimal("100"))
                .build();

        TransactionDto transaction = service.createTransaction(request);

        assertThat(transaction.getTxnId()).isNotNull();
        assertThat(accountRepository.findById(fromAccount.getId()).get().getBalance()).isEqualTo(new BigDecimal("9900.00"));
        assertThat(accountRepository.findById(toAccount.getId()).get().getBalance()).isEqualTo(new BigDecimal("5100.00"));
    }
}