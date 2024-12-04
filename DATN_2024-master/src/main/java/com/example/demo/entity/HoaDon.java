package com.example.demo.entity;

import com.example.demo.dto.hoadon.HoaDonRep;
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
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "MAHD", length = 10)
    private String maHD;

    @Column(name = "MAVOUNCHER", length = 10)
    private String maVoucher;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;


    @Column(name = "NGAYTHANHTOAN")
    private LocalDateTime ngayThanhToan;

    @Column(name = "NGAYNHANHANG")
    private LocalDateTime ngayNhanHang;

    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "GHICHU")
    private String ghiChu;

    @Column(name = "LOAIHD")
    private int loaiHD;

    @Column(name = "TENNGUOINHAN")
    private String tenNguoiNhan;

    @Column(name = "SDTNGUOINHAN")
    private String sdtNguoiNhan;

    @Column(name = "DIACHINGUOINHAN")
    private String diaChiNguoiNhan;

    @Column(name = "THANHTOAN")
    private Integer thanhtoan;

    @ManyToOne
    @JoinColumn(name = "IDNV")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "IDKH")
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

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    private List<ChiTietHoaDon> chiTietHoaDons;

    public HoaDonRep toResponse() {
        // Initialize tongTien to 0.0 to avoid NullPointerException
        Double tongTien = 0.0;

        // Iterate through each chiTietHoaDon to calculate the total
        for (ChiTietHoaDon chiTietHoaDon : chiTietHoaDons) {
            if (maVoucher!=null){}
            tongTien += Double.valueOf(chiTietHoaDon.getGiaSauGiam()) * chiTietHoaDon.getSoLuong();
        }

        // Return the HoaDonRep response with the calculated tongTien
        return new HoaDonRep(
                id,
                maHD,
                maVoucher,
                ngayTao,
                ngaySua,
                ghiChu,
                ngayThanhToan,
                ngayNhanHang,
                trangThai,
                loaiHD,
                thanhtoan,
                tenNguoiNhan,
                sdtNguoiNhan,
                diaChiNguoiNhan,
                khachHang != null ? khachHang.getTen() : null,
                khachHang != null ? khachHang.getSdt() : null,
                khachHang != null ? khachHang.getEmail() : null,
                nhanVien != null ? nhanVien.getTen() : null,
                String.valueOf(tongTien)
        );
    }



}
