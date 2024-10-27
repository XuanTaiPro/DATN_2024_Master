package com.example.demo.entity;

import com.example.demo.dto.lichsuhoadon.LichSuHoaDonResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "LICHSUHOADON")
public class LichSuHoaDon {
    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "TRANGTHAI")
    private int trangThai;

    @ManyToOne
    @JoinColumn(name = "IDHD") // Khóa ngoại tới bảng HOADON
    private HoaDon hoaDon;

    // Không cần thêm trường `tenNhanVien` vì bạn có thể lấy từ `hoaDon` và `nhanVien` của `HoaDon`
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public LichSuHoaDonResponse toResponse() {
        // Lấy thông tin tên nhân viên và mã hóa đơn từ quan hệ `HoaDon`
        String tenNhanVien = (hoaDon != null && hoaDon.getNhanVien() != null) ? hoaDon.getNhanVien().getTen() : "Chưa có hóa đơn";
        String maHD = hoaDon != null ? hoaDon.getMaHD() : null;
        String hoaDonId = hoaDon != null ? hoaDon.getId() : null;

        return new LichSuHoaDonResponse(
                id,
                ngayTao,
                trangThai,
                tenNhanVien,
                maHD,
                hoaDonId
        );
    }
}
