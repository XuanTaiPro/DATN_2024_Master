package com.example.demo.dto.danhgia;

import java.time.LocalDateTime;

import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhGiaRespOnline {
    private String id;

    private Integer sao;

    private String nhanXet;

    private Integer trangThai;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private SanPham sp;

    private ChiTietSanPham ctsp;

    private KhachHang kh;
}
