package com.example.demo.dto.loaivoucher;

import com.example.demo.entity.LoaiVoucher;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoaiVoucherRequest {

    private String id;

    private String ma;

    @Column(name = "Ten")
    @NotBlank(message = "Tên không được để trống")
    private String ten;

//    @NotBlank(message = "Ngày tạo không được để trống")
    private LocalDateTime ngayTao;

//    @NotBlank(message = "Ngày sửa không được để trống")
    private LocalDateTime ngaySua;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    @NotBlank(message = "Mô tả không được để trống")
    private String moTa;

    public LoaiVoucher toEntity(){
        return new LoaiVoucher(id,ma,ten,null,null,trangThai,moTa);
    }
}
