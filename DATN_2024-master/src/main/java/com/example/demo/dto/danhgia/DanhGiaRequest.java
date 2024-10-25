package com.example.demo.dto.danhgia;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhGiaRequest {

    private String id;

    @NotNull(message = "Sao không được để trống")
    @Min(value = 1, message = "Sao phải là số dương từ 1 đến 5")
    @Max(value = 5, message = "Sao phải là số dương từ 1 đến 5")
    private Integer sao;

    @Size(max = 255, message = "Nhận xét không được vượt quá 255 ký tự")
    private String nhanXet;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private Integer trangThai;

    @NotNull(message = "IDCTSP không được để trống")
    private String idCTSP;

    @NotNull(message = "IDKH không được để trống")
    private String idKH;

}
