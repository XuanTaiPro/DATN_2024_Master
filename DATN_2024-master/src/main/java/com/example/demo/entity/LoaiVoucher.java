package com.example.demo.entity;

import com.example.demo.dto.loaivoucher.LoaiVoucherResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "LOAIVOUCHER")
public class LoaiVoucher {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String ma;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "MOTA")
    private String moTa;

    public LoaiVoucherResponse toResponse() {
        return new LoaiVoucherResponse(id, ma, ten, ngayTao, ngaySua, trangThai, moTa);
    }
}
