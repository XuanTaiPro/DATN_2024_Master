package com.example.demo.dto.khachhang;

import com.example.demo.entity.KhachHang;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KhachHangRequest {
    private String id;

    private String ma;

    @NotBlank(message = "Tên không được để trống")
    private String ten;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String passw;

    @NotBlank(message = "Giới tính không được để trống")
    private String gioiTinh;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải bao gồm 10 chữ số")
    private String sdt;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

//    @NotBlank(message = "Ngày tạo không được để trống")
    private LocalDateTime ngayTao;

//    @NotBlank(message = "Ngày sửa không được để trống")
    private LocalDateTime ngaySua;

    public KhachHang toEntity() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new KhachHang(id, ma, ten, email, passw, gioiTinh, sdt, diaChi, trangThai, ngayTao, ngaySua);
    }
}
