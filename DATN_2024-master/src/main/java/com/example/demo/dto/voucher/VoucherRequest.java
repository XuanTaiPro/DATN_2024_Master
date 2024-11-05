package com.example.demo.dto.voucher;

import com.example.demo.entity.Voucher;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoucherRequest {

    private String id;

    // @NotBlank(message = "Mã Không được để trống")
    // @Pattern(regexp = "^VC\\d{3}$", message = "Mã phải có định dạng TBxxx (VD:
    // TB001, TB002,...)")
    private String ma;

    @NotBlank(message = "Tên Không được để trống")
    private String ten;

    @NotBlank(message = "Giảm giáKhông được để trống")
    private String giamGia;

    // @NotBlank(message = "Ngày tạo Không được để trống")
    private LocalDateTime ngayTao;

    // @NotBlank(message = "Ngày sửa Không được để trống")
    private LocalDateTime ngaySua;

    @NotBlank(message = "giảm min Không được để trống")
    private String giamMin;

    @NotBlank(message = "giảm max Không được để trống")
    private String giamMax;

    @NotBlank(message = "điều kiện Không được để trống")
    private String dieuKien;

    // @NotBlank(message = "ngày kết thúc Không được để trống")
    private String ngayKetThuc;

    @NotNull(message = "Số lượng Không được để trống")
    @Min(value = 1, message = "số lượng phải là số lớn hơn 0")
    private Integer soLuong;

    @NotNull(message = "Trạng thái Không được để trống")
    private Integer trangThai;

    @NotNull(message = "idLoaiVC Không được để trống")
    private String idLoaiVC;

    private List<String> idKH;

    public Voucher toEntity() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new Voucher(id, ma, ten, giamGia, ngayTao, ngaySua, giamMin, giamMax, dieuKien, ngayKetThuc, soLuong,
                trangThai, null);
    }
}
