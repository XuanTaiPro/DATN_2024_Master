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

<<<<<<< HEAD
    @NotNull(message = "Ngày nhập không được để trống")
    @PastOrPresent(message = "Ngày nhập phải là ngày hiện tại hoặc trong quá khứ")
=======
>>>>>>> 9f34d0937d006436e6db71eabd3bc05fbf64fb59
    private LocalDateTime ngayNhap;

    private Integer soLuong;

    private LocalDateTime hsd;

    private LocalDateTime nsx;

    private Integer trangThai;

    private String idSP;

    private List<Map<String, Object>> listLoHang;

    private List<String> linkAnhList; // Danh sách các liên kết hình ảnh

<<<<<<< HEAD

=======
>>>>>>> 9f34d0937d006436e6db71eabd3bc05fbf64fb59
}
