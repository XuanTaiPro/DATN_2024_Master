package com.example.demo.entity;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CHITIETHOADON")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChiTietHoaDon {

        @Id
        @Column(name = "ID")
        private String id;

        @Column(name = "MACTHD")
        private String maCTHD;

        @Column(name = "GIASAUGIAM")
        private String giaSauGiam;

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
        private HoaDon hoaDon;

        @ManyToOne
        @JoinColumn(name = "IDCTSP")
        private ChiTietSanPham chiTietSanPham;

        @PrePersist
        public void prePersist() {
                if (this.id == null) {
                        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                }
        }

        public ChiTietHoaDonRep toResponse() {
                double tienGiam = 0;
                double giaGiam = 0;
                if (chiTietSanPham.getSanPham().getGiamGia() != null &&
                                chiTietSanPham.getSanPham().getGiamGia().getNgayKetThuc().isAfter(LocalDateTime.now())
                                &&
                                chiTietSanPham.getSanPham().getGiamGia().getNgayBatDau()
                                                .isBefore(LocalDateTime.now())) {
                        giaGiam = Double.valueOf(chiTietSanPham.getSanPham().getGiamGia().getGiaGiam()) / 100;
                        tienGiam = Double.valueOf(chiTietSanPham.getSanPham().getGiamGia().getGiaGiam()) / 100
                                        * Double.valueOf(chiTietSanPham.getGia());
                }
                return new ChiTietHoaDonRep(
                                id,
                                maCTHD,
                                giaSauGiam,
                                soLuong,
                                giaBan,
                                String.valueOf(tienGiam),
                                trangThai,
                                ngayTao,
                                ngaySua,
                                ghiChu,
                                chiTietSanPham != null && chiTietSanPham.getSanPham() != null
                                                ? chiTietSanPham.getSanPham().getTenSP()
                                                : null,
                                chiTietSanPham != null ? chiTietSanPham.getGia() : null,
                                hoaDon != null ? hoaDon.getId() : null,
                                (chiTietSanPham != null && !chiTietSanPham.getAnhCTSP().isEmpty())
                                                ? chiTietSanPham.getAnhCTSP().get(0).getLink()
                                                : null,
                                chiTietSanPham != null ? chiTietSanPham.getSoNgaySuDung() : null,
                                chiTietSanPham != null ? chiTietSanPham.getSoLuong() : 0 // Make sure to handle null
                                                                                         // values for SoLuong
                );
        }


}
