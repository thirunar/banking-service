package com.finmid.bankingservice.service;

import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.exceptions.AccountNotFoundException;
import com.finmid.bankingservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public Account getAccount(UUID id) {
        return repository.findById(id)
                .orElseThrow(AccountNotFoundException::new);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public Account debit(UUID accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
        account.debit(amount);
        return account;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public Account credit(UUID accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
        account.credit(amount);
        return account;
    }

}
