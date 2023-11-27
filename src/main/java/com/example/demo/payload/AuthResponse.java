package com.example.demo.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthResponse extends BaseApiResponse {
    @JsonProperty("token")
    private String token;

    public AuthResponse(String token) {
        super(200, "Success");
        this.token = token;
    }

}
