package com.example.demo.dto.chitiethoadon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChiTietHoaDonRep {
    private String id;
    private String maCTHD;
    private String tongTien;
    private int soLuong;
    private String giaBan;
    private String tienGiam;
    private int trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String ghiChu;
    private String tenSP;
    private String gia;
    private String idHD;
    private String linkAnh;
    private String soNgaySuDung;
}
