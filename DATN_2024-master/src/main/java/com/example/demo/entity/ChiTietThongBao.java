package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "KH_TB")
public class ChiTietThongBao {
    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "IDKHACHHANG")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "IDTHONGBAO")
    private ThongBao thongBao;
}
