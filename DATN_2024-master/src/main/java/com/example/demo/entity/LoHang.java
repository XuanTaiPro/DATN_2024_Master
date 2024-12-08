package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "LOHANG")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoHang {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String ma;

    @Column(name = "SOLUONG")
    private Integer soLuong;

    @Column(name = "HSD")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime hsd;

    @Column(name = "NSX")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime nsx;

    @Column(name = "NGAYNHAP")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ngayNhap;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    @JsonIgnore // Bỏ qua tham chiếu này khi serialize
    private ChiTietSanPham ctsp;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
