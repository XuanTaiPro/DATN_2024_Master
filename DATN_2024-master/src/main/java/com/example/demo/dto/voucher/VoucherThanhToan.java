package com.example.demo.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoucherThanhToan {
    private String id;
    private String ten;
    private String giamGia;
    private String giamMin;
    private String giamMax;
    private String ngayKetThuc;
    private Integer soLuong;
    private Integer trangThai;
}
