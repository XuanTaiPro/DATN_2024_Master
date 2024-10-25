package com.example.demo.entity;

import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "THONGTINGIAOHANG")
public class ThongTinGiaoHang {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private String id;

    @Column(name = "SDTNGUOINHAN")
    private String sdtNguoiNhan;

    @Column(name = "TENNGUOINHAN")
    private String tenNguoiNhan;

    @Column(name = "DIACHINGUOINHAN")
    private String dcNguoiNhan;

    @Column(name = "NGAYTAO")
    private String ngayTao;

    @Column(name = "NGAYSUA")
    private String ngaySua;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "IDKHACHHANG")
    @JsonIgnore
    private KhachHang khachHang;

    public ThongTinGiaoHangResponse toResponse() {
        return new ThongTinGiaoHangResponse(id, sdtNguoiNhan, tenNguoiNhan, dcNguoiNhan, ngayTao, ngaySua,trangThai, khachHang.getTen(), khachHang.getEmail());
    }
}
