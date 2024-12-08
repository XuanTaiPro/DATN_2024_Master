package com.example.demo.entity;

import com.example.demo.dto.thongbao.ThongBaoResponse;
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
@Table(name = "THONGBAO")
public class ThongBao {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String ma;

    @Column(name = "NOIDUNG")
    private String noiDung;

    @Column(name = "NGAYGUI")
    private LocalDateTime ngayGui;

    @Column(name = "NGAYDOC")
    private LocalDateTime ngayDoc;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "IDKH")
    @JsonIgnore
    private KhachHang khachHang;

    public ThongBaoResponse toResponse() {
        return new ThongBaoResponse(id, ma, noiDung, ngayGui, ngayDoc, trangThai, khachHang.getTen(),
                khachHang.getEmail());
    }
}
