package com.example.demo.entity;

import com.example.demo.dto.giohangchitiet.GioHangChiTietOnline;
import com.example.demo.dto.giohangchitiet.GioHangChiTietResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "GIOHANGCHITIET")
public class GioHangChiTiet {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String ma;

    @Column(name = "SOLUONG")
    private Integer soLuong;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "NGAYTAO")
    private String ngayTao;

    @ManyToOne
    @JoinColumn(name = "IDKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "IDSPCT")
    @JsonIgnore
    private ChiTietSanPham chiTietSanPham;

    public GioHangChiTietResponse toResponse() {
        return new GioHangChiTietResponse(id, ma, soLuong, trangThai, ngayTao, khachHang.getTen(),
                chiTietSanPham.getMa(), chiTietSanPham.getGia());
    }

    public GioHangChiTietOnline toOnline() {
        return new GioHangChiTietOnline(id, ma, chiTietSanPham.getSoLuong(), soLuong,
                chiTietSanPham.getSanPham().getTenSP(),
                chiTietSanPham.getSoNgaySuDung(), chiTietSanPham.getGia());
    }
}
