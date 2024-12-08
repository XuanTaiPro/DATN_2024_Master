package com.example.demo.dto.anhCTSP;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnhCTSPRequest {
    private String id;

    @NotBlank(message = "Link ảnh không được để trống")
    @Size(max = 255, message = "Link ảnh không được vượt quá 255 ký tự")
    private String link;

    @NotBlank(message = "Tên ảnh không được để trống")
    @Size(max = 255, message = "Tên ảnh không được vượt quá 255 ký tự")
    private String ten;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 10, message = "Trạng thái không hợp lệ")
    private Integer trangThai;

    @NotNull(message = "Vui lòng nhập vào id chi tiết sản phẩm")
    private String idCTSP;
}
