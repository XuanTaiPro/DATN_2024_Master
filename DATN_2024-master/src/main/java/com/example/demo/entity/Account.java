package com.example.demo.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {

    @NotBlank(message = "Tài khoản không được để trống")
    private String email;

    @NotBlank(message = "PassWords không được để trống")
    private String passw;
}