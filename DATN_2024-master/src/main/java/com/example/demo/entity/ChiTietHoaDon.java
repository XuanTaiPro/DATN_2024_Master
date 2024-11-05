package com.example.demo.entity;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CHITIETHOADON")
public class ChiTietHoaDon {

    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "MACTHD")
    private String maCTHD;

    @Column(name = "TONGTIEN")
    private String tongTien;

    @Column(name = "SOLUONG")
    private int soLuong;

    @Column(name = "GIABAN")
    private String giaBan;

    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Column(name = "GHICHU")
    private String ghiChu;

    @ManyToOne
    @JoinColumn(name = "IDHOADON")
    @JsonBackReference
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    @JsonBackReference
    private ChiTietSanPham chiTietSanPham;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.getMaCTHD() == null) {
            // Tạo mã hóa đơn tự động
            this.maCTHD = "HD" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }
    }

    public ChiTietHoaDonRep toResponse() {
        double tienGiam=0;
        if (chiTietSanPham.getSanPham().getGiamGia() != null &&
                chiTietSanPham.getSanPham().getGiamGia().getNgayKetThuc().isAfter(LocalDateTime.now()) &&
                chiTietSanPham.getSanPham().getGiamGia().getNgayBatDau().isBefore(LocalDateTime.now())) {
            double giaGiam = Double.valueOf(chiTietSanPham.getSanPham().getGiamGia().getGiaGiam()) / 100;
            tienGiam=Double.valueOf(chiTietSanPham.getSanPham().getGiamGia().getGiaGiam())/100*Double.valueOf(chiTietSanPham.getGia());
        }
        return new ChiTietHoaDonRep(
                id,
                maCTHD,
                tongTien,
                soLuong,
                giaBan,
                String.valueOf(tienGiam),
                trangThai,
                ngayTao,
                ngaySua,
                ghiChu,
                chiTietSanPham != null ? chiTietSanPham.getSanPham().getTenSP() : null,
                chiTietSanPham != null ? chiTietSanPham.getGia() : null,
                hoaDon != null ? hoaDon.getId() : null,
                chiTietSanPham.getAnhCTSP().get(0).getLink(),
                chiTietSanPham.getSoNgaySuDung()
        );
    }


}
