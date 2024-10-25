package com.example.demo.dto.nhanvien;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NhanVienResponse {
    private String id;
    private String ma;
    private String ten;
    private String email;
    private String passw;
    private String gioiTinh;
    private String img;
    private String diaChi;
    private Integer trangThai;
    private String tenQuyen;
}
