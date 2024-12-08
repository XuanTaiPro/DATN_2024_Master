package com.example.demo.dto.giohangchitiet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GioHangChiTietResponse {

    private String id;

    private String ma;

    private Integer soLuong;

    private Integer trangThai;

    private String ngayTao;

    private String tenKH;

    private String maCTSP;

    private String giaCTSP;
}
