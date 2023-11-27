package com.example.demo.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseApiResponse {
    @JsonProperty("result_code")
    private int resultCode;
    @JsonProperty("result_text")
    private String resultText;
}
