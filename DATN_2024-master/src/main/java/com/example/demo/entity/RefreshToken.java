package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "REFRESHTOKEN")
public class RefreshToken {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private String id;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "THOIGIANHETHAN")
    private String thoiGianHetHan;

    @OneToOne
    @JoinColumn(name = "IDNV")
    private NhanVien nhanVien;
}
