package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "GIAMGIA")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Bỏ qua proxy của Hibernate
public class GiamGia {
    @Id
    @Column(name = "id")
    private String id;

    @Size(max = 10, message = "Mã giảm giá không được vượt quá 10 ký tự")
    @Column(name = "ma")
    private String ma;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    @Column(name = "ten")
    private String ten;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;
    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    // @FutureOrPresent(message = "Ngày bắt đầu phải là ngày hiện tại hoặc tương
    // lai")
    @Column(name = "ngayBatDau")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Định dạng cho ngày bắt đầu
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    // @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    @Column(name = "ngayKetThuc")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Định dạng cho ngày kết thúc
    private LocalDateTime ngayKetThuc;

    @NotBlank(message = "Giá giảm không được để trống")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?%?$", message = "Giá giảm phải là số dương, có thể có tối đa hai chữ số thập phân và có thể có ký tự '%' ở cuối")
    @Column(name = "giaGiam")
    private String giaGiam;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "trangThai")
    private Integer trangThai;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    @Column(name = "moTa")
    private String moTa;

    @OneToMany(mappedBy = "giamGia", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonIgnore
    private List<SanPham> listSanPham = new ArrayList<>();
}
