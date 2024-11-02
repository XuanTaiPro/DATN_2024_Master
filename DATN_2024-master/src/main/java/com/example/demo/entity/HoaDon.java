package com.example.demo.entity;

import com.example.demo.dto.hoadon.HoaDonRep;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "HOADON")
public class HoaDon {
    @Id
    @Column(name = "ID")
    private String id ;

    @Column(name = "MAHD", length = 10)
    private String maHD;

    @Column(name = "MAVOUNCHER", length = 10)
    private String maVoucher;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Column(name = "THANHTIEN")
    private String thanhTien;

    @Column(name = "NGAYTHANHTOAN")
    private LocalDateTime ngayThanhToan;

    @Column(name = "NGAYNHANHANG")
    private LocalDateTime ngayNhanHang;

    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "LOAIHD")
    private int loaiHD;

    @Column(name = "PHIVANCHUYEN")
    private String phiVanChuyen;

    @Column(name = "TENNGUOINHAN")
    private String tenNguoiNhan;

    @Column(name = "SDTNGUOINHAN")
    private String sdtNguoiNhan;

    @Column(name = "DIACHINGUOINHAN")
    private String diaChiNguoiNhan;

    @ManyToOne
    @JoinColumn(name = "IDNV")
    @JsonBackReference
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "IDKH")
    @JsonBackReference
    private KhachHang khachHang;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.maHD == null) {
            // Tạo mã hóa đơn tự động
            this.maHD = "HD" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }
    }

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ChiTietHoaDon> chiTietHoaDons;

    public HoaDonRep toResponse() {
        return new HoaDonRep(
                id,
                maHD,
                maVoucher,
                ngayTao,
                ngaySua,
                thanhTien,
                ngayThanhToan,
                ngayNhanHang,
                trangThai,
                loaiHD,
                phiVanChuyen,
                tenNguoiNhan,
                sdtNguoiNhan,
                diaChiNguoiNhan,
                khachHang != null ? khachHang.getTen() : null,
                khachHang != null ? khachHang.getSdt() : null,
                khachHang != null ? khachHang.getEmail() : null,
                nhanVien != null ? nhanVien.getTen() : null,
                chiTietHoaDons
        );
    }

}

