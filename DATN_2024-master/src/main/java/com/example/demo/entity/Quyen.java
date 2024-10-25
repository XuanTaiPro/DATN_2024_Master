package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "QUYEN")
public class Quyen {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private String id;

    @Column(name = "TEN")
    private String ten;

    @Column(name = "NGAYTAO")
    private String ngayTao;

    @Column(name = "NGAYSUA")
    private String ngaySua;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;
}
