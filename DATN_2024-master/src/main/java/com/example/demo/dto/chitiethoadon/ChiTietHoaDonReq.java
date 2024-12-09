package com.example.demo.dto.chitiethoadon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChiTietHoaDonReq {

    private String id;

    private String maCTHD;

    private String tongTien;

    private int soLuong;

    private String giaBan;

    private int trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    private String ghiChu;

    private String idHD;

    private String idCTSP;
}
