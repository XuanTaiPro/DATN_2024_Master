package com.example.demo.dto.thongbao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class  ThongBaoResponse {

    private String id;

    private String ma;

    private String noiDung;

    private LocalDateTime ngayGui;

    private LocalDateTime ngayDoc;

    private Integer trangThai;

    private String tenKH;

    private String emailKH;
}
