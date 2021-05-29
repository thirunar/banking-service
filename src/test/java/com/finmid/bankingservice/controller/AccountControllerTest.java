package com.finmid.bankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finmid.bankingservice.dto.AccountDto;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.exceptions.AccountNotFoundException;
import com.finmid.bankingservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    private static final UUID ACCOUNT_ID = UUID.randomUUID();

    private static final String ACCOUNT_ENDPOINT = "/v1/account";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void shouldCreateAnAccount() throws Exception {
        AccountDto dto = AccountDto.builder().balance(new BigDecimal("100000.00")).build();
        when(accountService.createAccount(any())).thenReturn(Account.builder()
                .id(ACCOUNT_ID).balance(new BigDecimal("100000.00"))
                .build());

        mockMvc.perform(post(ACCOUNT_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/account/" + ACCOUNT_ID));

        verify(accountService).createAccount(argThat(account -> {
            assertThat(account.getBalance()).isEqualTo(new BigDecimal("100000.00"));
            return true;
        }));
    }

    @Test
    void shouldCreateAnAccountWhenBalanceIsNotSpecified() throws Exception {
        AccountDto dto = AccountDto.builder().build();
        when(accountService.createAccount(any())).thenReturn(Account.builder()
                .id(ACCOUNT_ID)
                .build());

        mockMvc.perform(post(ACCOUNT_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/account/" + ACCOUNT_ID));

        verify(accountService).createAccount(argThat(account -> {
            assertThat(account.getBalance()).isNull();
            return true;
        }));
    }

    @Test
    void shouldGetAnAccount() throws Exception {
        when(accountService.getAccount(any())).thenReturn(Account.builder()
                .id(ACCOUNT_ID).balance(new BigDecimal("100000.00"))
                .build());

        mockMvc.perform(get(ACCOUNT_ENDPOINT +"/" + ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(ACCOUNT_ID.toString())))
                .andExpect(jsonPath("$.balance", equalTo(100000.00)));

        verify(accountService).getAccount(ACCOUNT_ID);
    }

    @Test
    void shouldReturnNotFoundWhenTheAccountNotExists() throws Exception {
        doThrow(AccountNotFoundException.class).when(accountService).getAccount(any());

        mockMvc.perform(get(ACCOUNT_ENDPOINT +"/" + ACCOUNT_ID))
                .andExpect(status().isNotFound());

        verify(accountService).getAccount(ACCOUNT_ID);
    }

}