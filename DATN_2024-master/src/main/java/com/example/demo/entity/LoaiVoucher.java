package com.example.demo.entity;

import com.example.demo.dto.loaivoucher.LoaiVoucherResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private String ngayTao;

    @Column(name = "NGAYSUA")
    private String ngaySua;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "MOTA")
    private String moTa;

    public LoaiVoucherResponse toResponse(){
        return new LoaiVoucherResponse(id,ma,ten,ngayTao,ngaySua,trangThai,moTa);
    }
}
