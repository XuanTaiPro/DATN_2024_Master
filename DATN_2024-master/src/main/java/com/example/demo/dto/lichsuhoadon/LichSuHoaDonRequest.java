package com.example.demo.dto.lichsuhoadon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LichSuHoaDonRequest {
    private String id;

    private LocalDateTime ngayTao;

    @NotNull(message = "Trạng thái không được để trống")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private int trangThai;
    //    @NotNull(message = "Nhân Viên không được để trống")
//    private String idNV;
    @NotNull(message = "Hóa đơn không được để trống")
    private String idHD;

}
