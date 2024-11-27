package com.example.demo.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailRequest {
    private String emailNguoiNhan;
    private String tieuDe;
    private String noiDung;
}
