package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "THUONGHIEU")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThuongHieu {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten")
    private String ten;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @Column(name = "trangThai")
    private Integer trangThai;

    @Column(name = "xuatXu")
    private String xuatXu;

    @Column(name = "moTa")
    private String moTa;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public ThuongHieu toResponse() {
        return new ThuongHieu(id, ma, ten, ngayTao, ngaySua, trangThai, xuatXu, moTa);
    }
}