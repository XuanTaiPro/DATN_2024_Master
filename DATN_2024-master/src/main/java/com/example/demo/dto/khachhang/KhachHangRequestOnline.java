package com.example.demo.dto.khachhang;

import com.example.demo.entity.KhachHang;
import jakarta.validation.constraints.*;

import java.util.UUID;

public class KhachHangRequestOnline {
    private String id;

    @NotBlank(message = "Tên không được để trống")
    private String ten;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Giới tính không được để trống")
    private String gioiTinh;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải bao gồm 10 chữ số")
    private String sdt;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    public KhachHang toEntity() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new KhachHang(id, null, ten, email, null, gioiTinh, sdt, diaChi, null, null, null);
    }
}
