package com.finmid.bankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void shouldCreateATransaction() throws Exception {
        TransactionDto request = TransactionDto.builder().build();
        UUID id = UUID.randomUUID();
        when(transactionService.createTransaction(any())).thenReturn(request.toBuilder().txnId(id).build());

        mockMvc.perform(post("/v1/transaction")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/transaction/" + id));

        verify(transactionService).createTransaction(request);
    }

}