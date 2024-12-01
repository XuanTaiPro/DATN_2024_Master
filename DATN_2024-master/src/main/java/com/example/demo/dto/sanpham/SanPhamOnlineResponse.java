package com.example.demo.dto.sanpham;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamOnlineResponse {
    private String id;

    private String tenSP;

    private String gia;

    private String giaGiam;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    private String linkAnh;
}
