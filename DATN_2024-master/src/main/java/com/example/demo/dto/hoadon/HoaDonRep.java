package com.example.demo.dto.hoadon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonRep {
    private String id;
    private String maHD;
    private String maVoucher;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String ghiChu;
    private LocalDateTime ngayThanhToan;
    private LocalDateTime ngayNhanHang;
    private int trangThai;
    private int loaiHD;
    private String phiVanChuyen;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiNguoiNhan;
    private String tenKH;
    private String sdt;
    private String email;
    private String tenNV;
}



