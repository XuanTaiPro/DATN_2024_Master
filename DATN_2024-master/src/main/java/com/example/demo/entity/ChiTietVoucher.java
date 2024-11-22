package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "CHITIETVOUCHER")
public class ChiTietVoucher {
    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "IDKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "IDVC")
    private Voucher voucher;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;
}
