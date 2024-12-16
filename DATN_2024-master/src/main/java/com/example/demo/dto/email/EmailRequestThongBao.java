package com.example.demo.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailRequestThongBao {
    private List<String> emailNguoiNhan;
    private String tieuDe;
    private String noiDung;
}
