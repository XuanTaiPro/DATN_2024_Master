package com.example.demo.dto.thongtingiaohang;

import com.example.demo.entity.ThongTinGiaoHang;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThongTinGiaoHangRequest {
    private String id;

    private String sdtNguoiNhan;

    @NotBlank(message = "Tên người nhận Không được để trống")
    private String tenNguoiNhan;

    @NotBlank(message = "Địa chỉ người nhận Không được để trống")
    private String dcNguoiNhan;

    // @NotBlank(message = "Ngày tạo Không được để trống")
    private LocalDateTime ngayTao;

    // @NotBlank(message = "Ngày sửa Không được để trống")
    private LocalDateTime ngaySua;

    private Integer trangThai;

    @NotNull(message = "idKH Không được để trống")
    private String idKH;

    public ThongTinGiaoHang toEntity() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new ThongTinGiaoHang(id, sdtNguoiNhan, tenNguoiNhan, dcNguoiNhan, null, null, trangThai, null);
    }
}
