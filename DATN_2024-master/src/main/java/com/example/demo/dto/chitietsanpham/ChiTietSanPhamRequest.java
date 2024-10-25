package com.example.demo.dto.chitietsanpham;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChiTietSanPhamRequest {

//    @Size(max = 10, message = "Mã không được vượt quá 10 ký tự")
////    @Pattern(regexp = "^CTSP[A-Z0-9]{6}$", message = "Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!")
//    private String ma;

    private String id;

    @NotBlank(message = "Giá không được để trống")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Giá phải là số dương, có thể có tối đa hai chữ số thập phân")
    private String gia;

    @NotBlank(message = "Số ngày sử dụng không được để trống")
    @Size(max = 255, message = "Số ngày sử dụng không được vượt quá 255 ký tự")
    private String soNgaySuDung;

    @NotNull(message = "Ngày sản xuất không được để trống")
    @PastOrPresent(message = "Ngày sản xuất phải là ngày hiện tại hoặc trong quá khứ")
    private LocalDateTime ngaySanXuat;

    @NotNull(message = "Hạn sử dụng không được để trống")
    @FutureOrPresent(message = "Hạn sử dụng phải là ngày hiện tại hoặc trong tương lai")
    private LocalDateTime hsd;

    @NotNull(message = "Ngày nhập không được để trống")
    @PastOrPresent(message = "Ngày nhập phải là ngày hiện tại hoặc trong quá khứ")
    private LocalDateTime ngayNhap;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer soLuong;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private Integer trangThai;

    @NotNull(message = "Vui lòng nhập vào id sản phẩm")
    private String idSP;

    private List<String> linkAnhList; // Danh sách các liên kết hình ảnh



}
