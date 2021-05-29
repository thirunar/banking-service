package com.finmid.bankingservice.service;

import com.finmid.bankingservice.ContainerInitializer;
import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.repository.AccountRepository;
import com.finmid.bankingservice.repository.TransactionRepository;
import com.finmid.bankingservice.service.AccountService;
import com.finmid.bankingservice.service.TransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = ContainerInitializer.class)
class TransferServiceITest {

    @Autowired
    private TransferService service;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account firstAccount;
    private Account secondAccount;

    @BeforeEach
    void setUp() {
        firstAccount = accountService.createAccount(Account.builder().balance(new BigDecimal("10000.00")).build());
        secondAccount = accountService.createAccount(Account.builder().balance(new BigDecimal("5000.00")).build());
    }

    @Test
    void shouldSaveTheTransactionWithUpdatedBalance() {
        TransactionDto request = TransactionDto.builder()
                .fromAccountId(firstAccount.getId())
                .toAccountId(secondAccount.getId())
                .amount(new BigDecimal("100"))
                .build();

        TransactionDto transaction = service.createTransaction(request);

        assertThat(transaction.getTxnId()).isNotNull();

        Account fromAccount = accountRepository.findById(this.firstAccount.getId()).get();
        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("9900.00"));
        assertThat(fromAccount.getVersion()).isEqualTo(1);

        Account toAccount = accountRepository.findById(this.secondAccount.getId()).get();
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("5100.00"));
        assertThat(toAccount.getVersion()).isEqualTo(1);
    }

    @Test
    void shouldUpdateTheBalancesOfAccountWithOptimisticLock() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        TransactionDto request = TransactionDto.builder()
                .fromAccountId(firstAccount.getId())
                .toAccountId(secondAccount.getId())
                .amount(new BigDecimal("10"))
                .build();
        TransactionDto anotherRequest = TransactionDto.builder()
                .fromAccountId(secondAccount.getId())
                .toAccountId(firstAccount.getId())
                .amount(new BigDecimal("5"))
                .build();

        for (int index = 0; index < 5; index++) {
            executor.execute(() -> service.createTransaction(request));
            executor.execute(() -> service.createTransaction(anotherRequest));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        Account fromAccount = accountService.getAccount(this.firstAccount.getId());
        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("9975.00"));
        assertThat(fromAccount.getVersion()).isEqualTo(10);

        Account toAccount = accountService.getAccount(this.secondAccount.getId());
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("5025.00"));
        assertThat(toAccount.getVersion()).isEqualTo(10);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
    }
}