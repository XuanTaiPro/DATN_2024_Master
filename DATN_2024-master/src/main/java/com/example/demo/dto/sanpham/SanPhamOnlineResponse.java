package com.example.demo.dto.sanpham;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamOnlineResponse {
    private String id;

    private String tenSP;

    private float gia;
}
