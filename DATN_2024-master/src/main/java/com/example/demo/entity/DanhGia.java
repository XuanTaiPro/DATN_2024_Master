package com.example.demo.entity;

import com.example.demo.dto.danhgia.DanhGiaRespOnline;
import com.example.demo.dto.danhgia.DanhGiaRespone;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DANHGIA")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DanhGia {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "SAO")
    private Integer sao;

    @Column(name = "NHANXET")
    private String nhanXet;

    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "NGAYDANHGIA")
    private LocalDateTime ngayDanhGia;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private ChiTietSanPham chiTietSanPham;

    @ManyToOne
    @JoinColumn(name = "IDKH")
    private KhachHang khachHang;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public DanhGiaRespone toRespone() {
        return new DanhGiaRespone(id, sao, nhanXet, trangThai, ngayDanhGia, ngaySua,
                chiTietSanPham != null ? chiTietSanPham.getMa() : null, khachHang.getTen());
    }

    public DanhGiaRespOnline convertFromDanhGia() {
        return new DanhGiaRespOnline(
                id,
                sao,
                nhanXet,
                trangThai,
                ngayDanhGia,
                ngaySua,
                null,
                chiTietSanPham,
                khachHang);
    }
}
