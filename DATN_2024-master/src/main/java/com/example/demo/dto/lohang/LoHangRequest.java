package com.example.demo.dto.lohang;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoHangRequest {

    private String id;

    private Integer soLuong;

    private LocalDateTime ngayNhap;

    private LocalDateTime hsd;

    private LocalDateTime nsx;

    private String idCTSP;

}
