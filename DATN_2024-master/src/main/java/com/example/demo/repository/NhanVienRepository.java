package com.example.demo.repository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    boolean existsByMa(String ma);

    boolean existsByMaAndIdNot(String ma, String id);

    @Query("SELECT nv FROM NhanVien nv WHERE " +
            "(:ten IS NULL OR LOWER(nv.ten) LIKE LOWER(CONCAT('%', :ten, '%'))) AND " +
            "(:gioiTinh IS NULL OR nv.gioiTinh = :gioiTinh) AND " +
            "(:diaChi IS NULL OR nv.diaChi LIKE %:diaChi%) AND " +
            "(:trangThai IS NULL OR nv.trangThai = :trangThai)")
    List<NhanVien> timKiemVaLocNhanVien(@Param("ten") String ten,
                                        @Param("gioiTinh") String gioiTinh,
                                        @Param("diaChi") String diaChi,
                                        @Param("trangThai") Integer trangThai);


    @Query("SELECT nv FROM NhanVien nv WHERE nv.email = :email AND nv.passw = :passw ")
    NhanVien loginNV(@Param("email") String email , @Param("passw") String passw);

}
