package com.example.demo.entity;

import com.example.demo.dto.danhgia.DanhGiaRespOnline;
import com.example.demo.dto.danhgia.DanhGiaRespone;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DANHGIA")
@Data
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
                chiTietSanPham != null ? chiTietSanPham.getMa() : null, khachHang.getMa());
    }

    public DanhGiaRespOnline convertFromDanhGia() {
        return new DanhGiaRespOnline(
                id,
                null,
                null,
                null,
                ngayDanhGia,
                null,
                null,
                chiTietSanPham,
                khachHang);
    }
}
