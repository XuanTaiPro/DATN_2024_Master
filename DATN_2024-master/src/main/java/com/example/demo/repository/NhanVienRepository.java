package com.example.demo.repository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    boolean existsByMa(String ma);

    boolean existsByMaAndIdNot(String ma, String id);

    @Query("SELECT nv FROM NhanVien nv WHERE " +
            "(:ten IS NULL OR nv.ten LIKE %:ten%) AND " +
            "(:gioiTinh IS NULL OR :gioiTinh = '' OR nv.gioiTinh = :gioiTinh) AND " +
            "(:diaChi IS NULL OR :diaChi = '' OR nv.diaChi LIKE %:diaChi%) AND " +
            "(:tenQuyen IS NULL OR :tenQuyen = '' OR nv.quyen.ten LIKE %:tenQuyen%) AND" +
            "(:trangThai IS NULL OR nv.trangThai = :trangThai)")
    Page<NhanVien> timKiemVaLocNhanVien(
            @Param("ten") String ten,
            @Param("gioiTinh") String gioiTinh,
            @Param("diaChi") String diaChi,
            @Param("tenQuyen") String tenQuyen,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);

    @Query("SELECT nv FROM NhanVien nv WHERE nv.email = :email AND nv.passw = :passw ")
    NhanVien loginNV(@Param("email") String email, @Param("passw") String passw);

}
