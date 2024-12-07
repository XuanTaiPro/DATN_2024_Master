package com.example.demo.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LH_HD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoHangWithHoaDon {
    @Id
    private String id;

    @JoinColumn(name = "IDLOHANG")
    private LoHang loHang;

    @JoinColumn(name = "IDCTHD")
    private ChiTietHoaDon cthd;

    @Column(name = "SOLUONG")
    private Integer soLuong;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
