package com.example.demo.dto.thongbao;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.ThongBao;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThongBaoRequest {
    private String id;

//    @NotBlank(message = "Mã Không được để trống")
//    @Pattern(regexp = "^TB\\d{3}$", message = "Mã phải có định dạng TBxxx (VD: TB001, TB002,...)")
    private String ma;

    @NotBlank(message = "Nội dung Không được để trống")
    @Size(max = 100, message = "Mã khách hàng không được dài quá 100 ký tự")
    private String noiDung;

//    @NotBlank(message = "Ngày gửi Không được để trống")
    private LocalDateTime ngayGui;

//    @NotBlank(message = "ngày đọc Không được để trống")
    private LocalDateTime ngayDoc;

    @NotNull(message = "Trạng thái Không được để trống")
    private Integer trangThai;

    @NotNull(message = "idKH Không được để trống")
    private String idKH;

    public ThongBao toEntity(){
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return new ThongBao(id,ma,noiDung,null,null,trangThai,null);
    }

}
