package com.example.demo.entity;

import com.example.demo.dto.hinhthuchoadon.HinhThucHoaDonResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "HINHTHUCHOADON")
public class HinhThucHoaDon {
    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "MAGIAODICH")
    private String maGiaoDich;

    @Column(name = "NGAYTAO")
    private String ngayTao;

    @Column(name = "NGAYTHANHTOAN")
    private String ngayThanhToan;

    @Column(name = "SOTIENKHACHTRA")
    private String soTienKhachTra;

    @Column(name = "THANHTIEN")
    private String thanhTien;

    @Column(name = "NGAYCAPNHAT")
    private String ngayCapNhat;

    @Column(name = "GHICHU")
    private String ghiChu;

    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "HINHTHUCTHANHTOAN")
    private int hinhThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "IDHOADON")
    private HoaDon hoaDon;

    public HinhThucHoaDonResponse toResponse() {
        return new HinhThucHoaDonResponse(
                id,
                maGiaoDich,
                ngayTao,
                ngayThanhToan,
                soTienKhachTra,
                thanhTien,
                ngayCapNhat,
                ghiChu,
                trangThai,
                hinhThucThanhToan,
                this.hoaDon != null ? this.hoaDon.getId() : null

        );
    }


}
