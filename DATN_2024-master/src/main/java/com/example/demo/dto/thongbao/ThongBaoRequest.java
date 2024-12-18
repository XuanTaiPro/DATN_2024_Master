package com.example.demo.dto.thongbao;

import com.example.demo.entity.ThongBao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThongBaoRequest {
    private String id;
    private String ma;
    private String noiDung;
    private LocalDateTime ngayGui;
    private LocalDateTime ngayDoc;
    private Integer trangThai;

    private List<String> idKHs;

    public ThongBao toEntity() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new ThongBao(id, ma, noiDung, null, null, trangThai, null);
    }

}
