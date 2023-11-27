package com.example.demo.service;

import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;


    public Transaction createTransaction(String userFrom, String userTo) {
        return transactionRepository.saveAndFlush(Transaction.builder()
                .amount(1.1)
                .date(LocalDateTime.now())
                .transferFrom(userFrom)
                .transferTo(userTo)
                .build());
    }
}
