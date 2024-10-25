package com.example.demo.dto.giohangchitiet;

import com.example.demo.entity.GioHangChiTiet;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GioHangChiTietRequest {
    private String id;

//    @NotBlank(message = "Mã Không được để trống")
//    @Pattern(regexp = "^GHCT\\d{3}$", message = "Mã phải có định dạng TBxxx (VD: GHCT001, GHCT002,...)")
    private String ma;

    @NotNull(message = "Số lượng Không được để trống")
    @Min(value = 1,message = "số lượng phải là số lớn hơn 0")
    private Integer soLuong;

    @NotNull(message = "Trạng thái Không được để trống")
    private Integer trangThai;

    @NotBlank(message = "Ngày tạo Không được để trống")
    private String ngayTao;

    @NotNull(message = "id Khách hàng Không được để trống")
    private String idKH;

    @NotNull(message = "id Chi tiết sản phẩm Không được để trống")
    private String idCTSP;

    public GioHangChiTiet toEntity(){

        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new GioHangChiTiet(id,ma,soLuong,trangThai,ngayTao,null,null);
    }
}
