package com.example.demo.entity;

import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    @ManyToMany
    @JoinTable(
            name = "KH_TB",
            joinColumns = @JoinColumn(name = "IDTHONGBAO"),
            inverseJoinColumns = @JoinColumn(name = "IDKHACHHANG")
    )
    @JsonIgnore
    private List<KhachHang> khachHangs;

    public ThongBaoResponse toResponse() {
        return new ThongBaoResponse(id, ma, noiDung, ngayGui, ngayDoc, trangThai, khachHangs.stream()
                .map(KhachHang::getTen).collect(Collectors.joining(", ")),
                khachHangs.stream().map(KhachHang::getEmail).collect(Collectors.joining(", ")));
    }
}
