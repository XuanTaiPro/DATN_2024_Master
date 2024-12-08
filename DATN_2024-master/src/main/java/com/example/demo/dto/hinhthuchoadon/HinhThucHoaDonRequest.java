package com.example.demo.dto.hinhthuchoadon;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HinhThucHoaDonRequest {

    private String id;


    private String maGiaoDich;

    @NotNull(message = "Ngày tạo không được để trống")
    private String ngayTao;

    private String ngayThanhToan;
    private String soTienKhachTra;

    private String thanhTien;


    private String ngayCapNhat;

    private String ghiChu;

    @NotNull(message = "Trạng thái không được để trống")
    private int trangThai;

    @NotNull(message = "Hình thức thanh toán không được để trống")
    private int hinhThucThanhToan;

    @NotEmpty(message = "ID hóa đơn không được để trống")
    private String idHD;
}
