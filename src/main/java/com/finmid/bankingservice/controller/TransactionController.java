package com.finmid.bankingservice.controller;

import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private static final String TRANSACTION_PATH = "/v1/transaction";
    private final TransactionService service;

    @PostMapping(path = TRANSACTION_PATH)
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {

        TransactionDto savedTransaction = service.createTransaction(transactionDto);

        return ResponseEntity.created(URI.create(Paths.get(TRANSACTION_PATH + "/" + savedTransaction.getTxnId()).toString())).build();
    }
}
