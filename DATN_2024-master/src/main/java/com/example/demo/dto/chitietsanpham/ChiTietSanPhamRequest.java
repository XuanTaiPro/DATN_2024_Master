package com.example.demo.dto.chitietsanpham;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.demo.entity.LoHang;

@Getter
@Setter
public class ChiTietSanPhamRequest {

    // @Size(max = 10, message = "Mã không được vượt quá 10 ký tự")
    //// @Pattern(regexp = "^CTSP[A-Z0-9]{6}$", message = "Mã phải có định dạng
    // CTSPXXXXXX (X là chữ cái hoặc số)!")
    // private String ma;

    private String id;

    private String gia;

    private String soNgaySuDung;

    private LocalDateTime ngayNhap;

    private Integer soLuong;

    private LocalDateTime hsd;

    private LocalDateTime nsx;

    private Integer trangThai;

    private String idSP;

    private List<Map<String, Object>> listLoHang;

    private List<String> linkAnhList; // Danh sách các liên kết hình ảnh

}
