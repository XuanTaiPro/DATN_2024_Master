package com.example.demo.dto.thongtingiaohang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThongTinGiaoHangResponse {
    private String id;

    private String sdtNguoiNhan;

    private String tenNguoiNhan;

    private String dcNguoiNhan;

    private String ngayTao;

    private String ngaySua;

    private Integer trangThai;

    private String tenKH;

    private String emailKH;
}
