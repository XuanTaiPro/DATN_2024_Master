package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MailKH {
    private String customerName;
    private double amountPaid;
    private double totalAmount;
    private String idHD;
    private String email;
    private double discountAmount;
}
