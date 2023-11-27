package com.example.demo.errors.handler;

import com.example.demo.errors.exception.InvalidCredentialsException;
import com.example.demo.errors.exception.NotEnoughBalanceException;
import com.example.demo.service.ErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ErrorService errorService;

    @ExceptionHandler(NoSuchElementException.class)
    private ResponseEntity<?> noSuchElementHandler(NoSuchElementException exception) {
        return new ResponseEntity<>(errorService.makeBody(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> methodArgumentNotValidHandler(MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(errorService.makeBody(exception.getBindingResult()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    private ResponseEntity<?> invalidCredentialsHandler(InvalidCredentialsException exception) {
        return new ResponseEntity<>(errorService.makeBody(exception), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotEnoughBalanceException.class)
    private ResponseEntity<?> notEnoughBalanceHandler(NotEnoughBalanceException exception) {
        return new ResponseEntity<>(errorService.makeBody(exception), HttpStatus.NOT_ACCEPTABLE);
    }
}
