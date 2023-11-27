package com.example.demo.payload;

public class RegisterResponse extends BaseApiResponse{
    private String message;
    public RegisterResponse(String message) {
        super(200, "Success");
        this.message = message;
    }
}
