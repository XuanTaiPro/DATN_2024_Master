package com.example.demo.dto.chitietsanpham;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChiTietSanPhamRequest {

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
