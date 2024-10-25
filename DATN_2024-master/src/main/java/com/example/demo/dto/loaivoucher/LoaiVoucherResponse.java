package com.example.demo.dto.loaivoucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoaiVoucherResponse {

    private String id;

    private String ma;

    private String ten;

    private String ngayTao;

    private String ngaySua;

    private Integer trangThai;

    private String moTa;

}
