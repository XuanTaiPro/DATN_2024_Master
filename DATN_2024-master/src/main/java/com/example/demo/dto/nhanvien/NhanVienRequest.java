package com.example.demo.dto.nhanvien;

import com.example.demo.entity.NhanVien;
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
public class NhanVienRequest {

    private String id;

//    @NotBlank(message = "Mã Không được để trống")
//    @Pattern(regexp = "^NV\\d{3}$", message = "Mã phải có định dạng TBxxx (VD: NV001, NV002,...)")
    private String ma;

    @NotBlank(message = "Tên Không được để trống")
    private String ten;

    @Column(name = "EMAIL", unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(name = "PASSW")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String passw;

    @NotBlank(message = "Giới tính Không được để trống")
    private String gioiTinh;

//    @NotBlank(message = "IMG Không được để trống")
    private String img;

    @NotBlank(message = "Địa chỉ Không được để trống")
    private String diaChi;

    @NotNull(message = "Trạng thái Không được để trống")
    private Integer trangThai;

//    @NotBlank(message = "Ngày tạo Không được để trống")
    private LocalDateTime ngayTao;

//    @NotBlank(message = "Ngày sửa Không được để trống")
    private LocalDateTime ngaySua;

    @NotNull(message = "id quyền Không được để trống")
    private String idQuyen;


    public NhanVien toEntity() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new NhanVien(id, ma, ten, email, passw, gioiTinh, img, diaChi, trangThai,null,null, null);
    }
}
