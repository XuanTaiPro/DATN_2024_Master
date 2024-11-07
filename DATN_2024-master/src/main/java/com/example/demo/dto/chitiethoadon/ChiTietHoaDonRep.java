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
<<<<<<< HEAD
=======
    private String linkAnh;
    private String soNgaySuDung;
>>>>>>> 25f4e8127337f7d4a4676e740b7c7ca3f89950b0
}
