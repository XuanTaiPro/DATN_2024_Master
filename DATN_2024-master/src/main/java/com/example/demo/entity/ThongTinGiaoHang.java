package com.example.demo.entity;

import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangResponse;
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
@Table(name = "THONGTINGIAOHANG")
public class ThongTinGiaoHang {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private String id;

    @Column(name = "SDTNGUOINHAN")
    private String sdtNguoiNhan;

    @Column(name = "TENNGUOINHAN")
    private String tenNguoiNhan;

    @Column(name = "DIACHINGUOINHAN")
    private String dcNguoiNhan;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "IDKHACHHANG")
    @JsonIgnore
    private KhachHang khachHang;

    public ThongTinGiaoHangResponse toResponse() {
        return new ThongTinGiaoHangResponse(id, sdtNguoiNhan, tenNguoiNhan, dcNguoiNhan, ngayTao, ngaySua, trangThai,
                khachHang.getTen(), khachHang.getEmail());
    }
}
