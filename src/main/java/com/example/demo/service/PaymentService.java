package com.example.demo.service;

import com.example.demo.errors.exception.NotEnoughBalanceException;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.payload.BaseApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserService userService;

    private final TransactionService transactionService;

    public BaseApiResponse payment(){
        double amount = 1.1;
        User user = userService.getUser();
        if (user.getBalance() <= amount){
            throw new NotEnoughBalanceException("Not enough balance");
        }
        Transaction transaction = transactionService.createTransaction(user.getEmail(), "ewq@ewq.ewq");
        userService.applyTransaction(user, transaction);
        return new BaseApiResponse(200, "Success");
    }
}
