package com.example.demo.dto.hoadon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonReq {
    private String id;

    private String maHD;

    private String maVoucher;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private String thanhTien;

    private LocalDateTime ngayThanhToan;

    private LocalDateTime ngayNhanHang;

    @Min(value = 0, message = "Trạng thái phải lớn hơn hoặc bằng 0")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private int trangThai;

    @Min(value = 0, message = "Loại hóa đơn phải lớn hơn hoặc bằng 0")
    private int loaiHD;

    private String phiVanChuyen;

    private String tenNguoiNhan;

    private String sdtNguoiNhan;

    private String diaChiNguoiNhan;

    private String idKH;

    private String idNV;

}
