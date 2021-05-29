package com.finmid.bankingservice.service;

import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.entity.Transaction;
import com.finmid.bankingservice.exceptions.BalanceNotUpdatedException;
import com.finmid.bankingservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Integer MAXIMUM_NUMBER_OF_RETRIES = 10;

    private final TransactionRepository repository;
    private final AccountService accountService;

    @Transactional
    public TransactionDto transfer(TransactionDto transaction) {

        Transaction txn = Transaction.builder()
                .fromAccount(updateBalance(transaction.getFromAccountId(), transaction.getAmount(), accountService::debit))
                .toAccount(updateBalance(transaction.getToAccountId(), transaction.getAmount(), accountService::credit))
                .amount(transaction.getAmount()).build();

        Transaction savedTransaction = repository.save(txn);

        return TransactionDto.fromTransaction(savedTransaction);
    }

    private Account updateBalance(UUID accountId, BigDecimal amount, BiFunction<UUID, BigDecimal, Account> function) {
        int counter = 0;
        while (counter < MAXIMUM_NUMBER_OF_RETRIES) {
            try {
                counter++;
                return function.apply(accountId, amount);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.debug("Optimistic locking exception during credit");
            }
        }
        throw new BalanceNotUpdatedException();
    }

}
