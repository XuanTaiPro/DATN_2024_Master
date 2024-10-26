package com.example.demo.dto.loaivoucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoaiVoucherResponse {

    private String id;

    private String ma;

    private String ten;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private Integer trangThai;

    private String moTa;

}
