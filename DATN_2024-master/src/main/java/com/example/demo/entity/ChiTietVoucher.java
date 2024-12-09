package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
