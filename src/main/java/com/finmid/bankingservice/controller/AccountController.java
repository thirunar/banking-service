package com.finmid.bankingservice.controller;

import com.finmid.bankingservice.dto.AccountDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

import static java.nio.file.Paths.get;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private static final String ACCOUNT_PATH = "/v1/account";

    private final AccountService service;

    @PostMapping(path = ACCOUNT_PATH)
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDto account) {
        Account savedAccount = service.createAccount(Account.fromDto(account));

        return ResponseEntity.created(URI.create(get(ACCOUNT_PATH, savedAccount.getId().toString()).toString())).build();
    }

    @GetMapping(path = ACCOUNT_PATH + "/{id}")
    public AccountDto getAccount(@PathVariable UUID id) {
        return AccountDto.fromAccount(service.getAccount(id));
    }
}
