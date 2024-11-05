package com.example.demo.repository;

import com.example.demo.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface HoaDonRepo extends JpaRepository<HoaDon,String> {
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :maHD")
    Optional<HoaDon> findByMaHD(@Param("maHD") String maHD);
    @Query("SELECT hd FROM HoaDon hd WHERE (:trangThai IS NULL OR hd.trangThai = :trangThai) " +
            "AND (:tenKH IS NULL OR hd.khachHang.ten LIKE %:tenKH%) " +
            "AND (:loaiHD IS NULL OR hd.loaiHD = :loaiHD) " +
            "AND (:nhanVien IS NULL OR hd.nhanVien.ten =:nhanVien)")
    Page<HoaDon> findHDByFilters(@Param("trangThai") Integer trangThai,
                                 @Param("tenKH") String tenKH,
                                 @Param("loaiHD") Integer loaiHD,
                                 @Param("nhanVien") String nhanVien,
                                 Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd WHERE hd.khachHang.ten like:tenKH AND hd.loaiHD =:loaiHD AND hd.nhanVien.ten =:nhanVien")
    Page<HoaDon> searchHoaDon(@Param("tenKH")String tenKH,@Param("loaiHD")Integer loaiHD,@Param("nhanVien")String nhanVien,Pageable pageable);
}
