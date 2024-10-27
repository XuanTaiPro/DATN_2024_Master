package com.example.demo.repository;

import com.example.demo.entity.LichSuHoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LichSuHoaDonRepo extends JpaRepository<LichSuHoaDon, String> {
    @Query("SELECT lshd FROM LichSuHoaDon lshd WHERE (lshd.hoaDon.nhanVien.ten LIKE %:keyword% OR lshd.hoaDon.maHD LIKE %:keyword%)")
    Page<LichSuHoaDon> searchByTenNVOrMaHD(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT lshd FROM LichSuHoaDon lshd WHERE  lshd.id<>:id")
    LichSuHoaDon getByIdLS( @Param("id")String id);

}
