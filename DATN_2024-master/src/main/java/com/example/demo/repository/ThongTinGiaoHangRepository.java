package com.example.demo.repository;

import com.example.demo.entity.ThongTinGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThongTinGiaoHangRepository extends JpaRepository<ThongTinGiaoHang, String> {
    boolean existsByKhachHang_IdAndDcNguoiNhan(String khachHangId, String dcNguoiNhan);

    @Query("SELECT t FROM ThongTinGiaoHang t WHERE " +
            "(:tenNguoiNhan IS NULL OR t.tenNguoiNhan LIKE %:tenNguoiNhan%) OR " +
            "(:sdtNguoiNhan IS NULL OR t.sdtNguoiNhan = :sdtNguoiNhan)")
    List<ThongTinGiaoHang> searchByTenOrSdtNguoiNhan(@Param("tenNguoiNhan") String tenNguoiNhan,
                                                     @Param("sdtNguoiNhan") String sdtNguoiNhan);
}

