package com.example.demo.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoucherResponse {
    private String id;

    private String ma;

    private String ten;

    private String giamGia;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private String giamMin;

    private String giamMax;

    private String dieuKien;

    private String ngayKetThuc;

    private Integer soLuong;

    private Integer trangThai;

    private String tenLoaiVC;

}
