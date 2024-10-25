package com.example.demo.dto.thongtingiaohang;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.ThongTinGiaoHang;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThongTinGiaoHangRequest {
    private String id;

    @NotBlank(message = "SDT người nhận Không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    private String sdtNguoiNhan;

    @NotBlank(message = "Tên người nhận Không được để trống")
    private String tenNguoiNhan;

    @NotBlank(message = "Địa chỉ người nhận Không được để trống")
    private String dcNguoiNhan;

//    @NotBlank(message = "Ngày tạo Không được để trống")
    private String ngayTao;

//    @NotBlank(message = "Ngày sửa Không được để trống")
    private String ngaySua;

    @NotNull(message = "trạng thái Không được để trống")
    private Integer trangThai;

    @NotNull(message = "idKH Không được để trống")
    private String idKH;


    public ThongTinGiaoHang toEntity(){
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new ThongTinGiaoHang(id,sdtNguoiNhan,tenNguoiNhan,dcNguoiNhan,ngayTao,ngaySua,trangThai,null);
    }
}
