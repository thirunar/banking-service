package com.finmid.bankingservice.service;

import com.finmid.bankingservice.exceptions.AccountNotFoundException;
import com.finmid.bankingservice.model.Account;
import com.finmid.bankingservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
}
