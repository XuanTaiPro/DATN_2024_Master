package com.example.demo.dto.chitietsanpham;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChiTietSanPhamResponse {
    private String id;
    private String ma;
    private String gia;
    private String tienGiam;
    private String soNgaySuDung;
    private LocalDateTime ngayNhap;
    private int soLuong;
    private int trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String maSP;
    private String idSP;
    private List<String> linkAnhList; // Danh sách liên kết hình ảnh

}
