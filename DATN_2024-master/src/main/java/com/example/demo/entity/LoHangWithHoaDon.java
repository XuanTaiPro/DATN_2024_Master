package com.example.demo.entity;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LH_HD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoHangWithHoaDon {
    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "IDLOHANG")
    private LoHang loHang;

    @ManyToOne
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
