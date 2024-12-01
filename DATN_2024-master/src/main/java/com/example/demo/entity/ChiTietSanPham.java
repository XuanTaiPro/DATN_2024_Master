package com.example.demo.entity;

import com.example.demo.dto.chitietsanpham.ChiTietSanPhamResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "CHITIETSANPHAM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "gia")
    private String gia;

    @Column(name = "soNgaySuDung")
    private String soNgaySuDung;

    @Column(name = "ngayNhap")
    private LocalDateTime ngayNhap;

    @Column(name = "soLuong")
    private Integer soLuong;

    @Column(name = "trangThai")
    private int trangThai;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSP")
    @JsonIgnore // Bỏ qua tham chiếu này khi serialize
    private SanPham sanPham;

    @OneToMany(mappedBy = "chiTietSanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnhCTSP> anhCTSP = new ArrayList<>(); // Danh sách ảnh liên kết

    public ChiTietSanPhamResponse toChiTietSanPhamResponse() {
        double tienGiam = 0;
        List<String> linkAnhList = anhCTSP != null
                ? anhCTSP.stream().map(AnhCTSP::getLink).collect(Collectors.toList())
                : new ArrayList<>(); // Trả về danh sách rỗng nếu không có hình ảnh
        if (sanPham.getGiamGia() != null &&
                sanPham.getGiamGia().getNgayKetThuc().isAfter(LocalDateTime.now()) &&
                sanPham.getGiamGia().getNgayBatDau().isBefore(LocalDateTime.now())) {
            double giaGiam = Double.valueOf(sanPham.getGiamGia().getGiaGiam()) / 100;
            tienGiam = Double.valueOf(sanPham.getGiamGia().getGiaGiam()) / 100 * Double.valueOf(this.gia);
            gia = String.valueOf(Double.valueOf(this.gia) * (1 - giaGiam));
        }
        return new ChiTietSanPhamResponse(
                id,
                ma,
                gia,
                String.valueOf(tienGiam),
                soNgaySuDung,
                ngayNhap,
                soLuong,
                trangThai,
                ngayTao,
                ngaySua,
                sanPham.getMaSP(),
                sanPham.getId(),
                linkAnhList // Include the image link in the response
        );
    }
}
