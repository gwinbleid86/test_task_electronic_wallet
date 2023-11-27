package com.example.demo.errors.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    @Getter
    protected static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BaseException(String s) {
        super(s);
    }

}
