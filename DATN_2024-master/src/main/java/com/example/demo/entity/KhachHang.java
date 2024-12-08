package com.example.demo.entity;

import com.example.demo.dto.khachhang.KhachHangResponse;
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
@Table(name = "KHACHHANG")
public class KhachHang {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA", unique = true)
    private String ma;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PASSW")
    private String passw;

    @Column(name = "GIOITINH")
    private String gioiTinh;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "DIACHI")
    private String diaChi;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    public KhachHangResponse toResponse() {
        return new KhachHangResponse(id, ma, ten, email, passw, gioiTinh, sdt, diaChi, trangThai);
    }
}
