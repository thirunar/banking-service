package com.finmid.bankingservice.service;

import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.entity.Transaction;
import com.finmid.bankingservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountService accountService;

    public TransactionDto createTransaction(TransactionDto transaction) {
        Account fromAccount = accountService.getAccount(transaction.getFromAccountId());
        Account toAccount = accountService.getAccount(transaction.getToAccountId());

        Transaction txn = Transaction.builder()
                .fromAccount(fromAccount).toAccount(toAccount)
                .amount(transaction.getAmount()).build();

        Transaction savedTransaction = repository.save(txn);

        return TransactionDto.fromTransaction(savedTransaction);
    }
}
