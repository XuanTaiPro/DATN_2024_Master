package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "DANHMUC")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Bỏ qua proxy của Hibernate
@JsonIgnoreProperties({"listSanPham"})
public class DanhMuc {
    @Id
    @Column(name = "ID")
    private String id;

    @Size(max = 10, message = "Mã danh mục không được vượt quá 10 ký tự")
//    @Pattern(regexp = "^DM[A-Z0-9]{8}$", message = "Mã phải có định dạng DMXXXXXXXX (X là chữ cái hoặc số)!")
    @Column(name = "MA")
    private String ma;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]+$", message = "Tên danh mục chỉ được chứa chữ cái!")
    @Column(name = "TEN")
    private String ten;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SanPham> listSanPham = new ArrayList<>(); // Danh sách ảnh liên kết


    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
