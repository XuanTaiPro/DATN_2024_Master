package com.example.demo.entity;

import com.example.demo.dto.sanpham.SanPhamResponse;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "SANPHAM")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"danhMuc"})
public class SanPham {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ma")
    private String maSP;

    @Column(name = "ten")
    private String tenSP;

    @Column(name = "thanhPhan")
    private String thanhPhan;

    @Column(name = "congDung")
    private String congDung;

    @Column(name = "tuoiMin")
    private Integer tuoiMin;

    @Column(name = "tuoiMax")
    private Integer tuoiMax;

    @Column(name = "hdsd")
    private String hdsd;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @Column(name = "trangThai")
    private Integer trangThai;

    @Column(name = "moTa")
    private String moTa;

    @ManyToOne
    @JsonBackReference // Bỏ qua trường này khi tuần tự hóa JSON để tránh vòng lặp
    @JoinColumn(name = "idDanhMuc")
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "idThuongHieu")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "idGiamGia")
    @JsonBackReference // Ngăn chặn việc serialize ngược lại
    private GiamGia giamGia;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Bỏ qua danh sách chi tiết sản phẩm khi tuần tự hóa
    private List<ChiTietSanPham> listCTSP = new ArrayList<>(); // Danh sách ảnh liên kết

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public SanPhamResponse toResponse() {
        String giamGiaTen = (giamGia != null) ? giamGia.getTen() : null; // Check for null in giamGia
        String giaDuocGiam = giamGia != null ? giamGia.getGiaGiam() : "0";
        // Calculate total quantity where trangThai = 1
        int tongSoLuong = listCTSP.stream()
                .filter(ctsp -> ctsp.getTrangThai() == 1) // Only include items with trangThai = 1
                .mapToInt(ChiTietSanPham::getSoLuong)
                .sum();

        // Check if listCTSP and AnhCTSP are not empty before accessing
        String anhLink = null;
        if (!listCTSP.isEmpty()) {
            ChiTietSanPham firstCTSP = listCTSP.get(0);
            if (firstCTSP != null && !firstCTSP.getAnhCTSP().isEmpty()) {
                anhLink = firstCTSP.getAnhCTSP().get(0).getLink();
            }
        }

        return new SanPhamResponse(
                id,
                maSP,
                tenSP,
                thanhPhan,
                congDung,
                tuoiMin,
                tuoiMax,
                tongSoLuong, // Include total quantity
                ngayTao,
                ngaySua,
                hdsd,
                trangThai,
                moTa,
                danhMuc != null ? danhMuc.getTen() : null, // Check for null in danhMuc
                thuongHieu != null ? thuongHieu.getTen() : null, // Check for null in thuongHieu
                giamGiaTen,
                giaDuocGiam,
                anhLink);
    }

}
