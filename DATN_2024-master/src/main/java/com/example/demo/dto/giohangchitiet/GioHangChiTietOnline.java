package com.example.demo.dto.giohangchitiet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GioHangChiTietOnline {
    private String id;

    private String ma;

    private Integer soLuong;

    private Integer soLuongTrongGio;

    private String tenSanPhamTrongGio;

    private String soNgaySuDung;

    private String gia;
}
