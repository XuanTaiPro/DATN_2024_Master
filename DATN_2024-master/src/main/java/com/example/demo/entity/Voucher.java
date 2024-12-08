package com.example.demo.entity;

import com.example.demo.dto.voucher.VoucherResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "VOUCHER")
public class Voucher {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String ma;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "GIAGIAM")
    private String giamGia;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Column(name = "GIAMMIN")
    private String giamMin;

    @Column(name = "GIAMMAX")
    private String giamMax;

    @Column(name = "DIEUKIEN")
    private String dieuKien;

    @Column(name = "NGAYKETTHUC")
    private String ngayKetThuc;

    @Column(name = "SOLUONG")
    private Integer soLuong;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "IDLOAIVC")
    @JsonIgnore
    private LoaiVoucher loaiVoucher;

    public VoucherResponse toResponse() {
        return new VoucherResponse(id, ma, ten, giamGia, ngayTao, ngaySua, giamMin, giamMax, dieuKien, ngayKetThuc,
                soLuong, trangThai, loaiVoucher.getTen());
    }
}
