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
            "AND (:nhanVien IS NULL OR :nhanVien ='' OR hd.nhanVien.ten = :nhanVien) " +
            "AND (:tenKH IS NULL OR hd.khachHang.ten LIKE %:tenKH% OR hd.khachHang.sdt LIKE %:tenKH%) " +
            "AND (hd.khachHang IS NULL OR hd.khachHang IS NOT NULL)")
    Page<HoaDon> findHDByFilters(@Param("trangThai") Integer trangThai,
                                 @Param("loaiHD") Integer loaiHD,
                                 @Param("nhanVien") String nhanVien,
                                 @Param("tenKH") String tenKH,
                                 Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd WHERE (:trangThai IS NULL OR hd.trangThai = :trangThai) " +
            "AND (:loaiHD IS NULL OR hd.loaiHD = :loaiHD) " +
            "AND (:nhanVien IS NULL OR hd.nhanVien.ten = :nhanVien) " +
            "AND (hd.khachHang IS NULL OR hd.khachHang IS NOT NULL)")
    Page<HoaDon> findHDByFiltersNullTenKH(@Param("trangThai") Integer trangThai,
                                          @Param("loaiHD") Integer loaiHD,
                                          @Param("nhanVien") String nhanVien,
                                          Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai = :trangThai ORDER BY hd.ngayTao DESC")
    List<HoaDon> getHDTaiQuay(@Param("trangThai") Integer trangThai);

    @Query(value = "SELECT " +
            "SUM(" +
            "    CAST(COALESCE(TRY_CAST(c.giaSauGiam AS decimal(18, 2)), 0) AS decimal(18, 2)) * " +
            "    CAST(COALESCE(TRY_CAST(c.soLuong AS int), 0) AS int) * " +
            "    (1 - COALESCE(v.giaGiam, 0) / 100.0)" +
            ") AS totalAmount, " +
            "CAST(h.ngayThanhToan AS DATE) AS ngayThanhToan " +
            "FROM HOADON h " +
            "JOIN CHITIETHOADON c ON h.id = c.idHoaDon " +
            "LEFT JOIN Voucher v ON h.idVC = v.id " +
            "WHERE h.trangThai = 3 " +
            "AND (:year IS NULL OR YEAR(h.ngayThanhToan) = :year) " +
            "AND (:month IS NULL OR MONTH(h.ngayThanhToan) = :month) " +
            "AND (:week IS NULL OR DATEPART(WEEK, h.ngayThanhToan) = :week) " +
            "AND (:day IS NULL OR DAY(h.ngayThanhToan) = :day) " +
            "GROUP BY CAST(h.ngayThanhToan AS DATE)", nativeQuery = true)
    List<Object[]> getTotalAmountAndNgayThanhToan(@Param("year") Integer year,
                                                  @Param("month") Integer month,
                                                  @Param("week") Integer week,
                                                  @Param("day") Integer day);

    @Query("SELECT hd FROM HoaDon hd WHERE hd.khachHang IS NULL")
    List<HoaDon> getHDNullKH();

    @Query("select hd from HoaDon hd where hd.khachHang.id = :id")
    List<HoaDon> getHDByCustomerId(@Param("id") String id);

    @Query("select hd from HoaDon hd where hd.sdtNguoiNhan = :sdt")
    List<HoaDon> getHDBySDT(@Param("sdt") String sdt);
}
