package com.example.demo.repository;

import com.example.demo.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HoaDonRepo extends JpaRepository<HoaDon, String> {
        @Query("SELECT h FROM HoaDon h WHERE h.maHD = :maHD")
        Optional<HoaDon> findByMaHD(@Param("maHD") String maHD);

        @Query("SELECT hd FROM HoaDon hd WHERE (:trangThai IS NULL OR hd.trangThai = :trangThai) " +
                        "AND (:loaiHD IS NULL OR hd.loaiHD = :loaiHD) " +
                        "AND (:nhanVien IS NULL OR hd.nhanVien.ten = :nhanVien) " +
                        "AND (hd.khachHang IS NULL OR hd.khachHang IS NOT NULL )")
        Page<HoaDon> findHDByFilters(@Param("trangThai") Integer trangThai,
                        // @Param("tenKH") String tenKH,
                        @Param("loaiHD") Integer loaiHD,
                        @Param("nhanVien") String nhanVien,
                        Pageable pageable);

        @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai = :trangThai ORDER BY hd.ngayTao DESC")
        List<HoaDon> getHDTaiQuay(@Param("trangThai") Integer trangThai);

        @Query("SELECT hd FROM HoaDon hd WHERE hd.khachHang IS NULL")
        List<HoaDon> getHDNullKH();

        @Query("select hd from HoaDon hd where hd.khachHang.id = :id")
        List<HoaDon> getHDByCustomerId(@Param("id") String id);

        @Query("select hd from HoaDon hd where hd.sdtNguoiNhan = :sdt")
        List<HoaDon> getHDBySDT(@Param("sdt") String sdt);
}
