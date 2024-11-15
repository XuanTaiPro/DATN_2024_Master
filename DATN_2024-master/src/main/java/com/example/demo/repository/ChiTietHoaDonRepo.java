package com.example.demo.repository;

import com.example.demo.entity.ChiTietHoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietHoaDonRepo extends JpaRepository<ChiTietHoaDon, String> {

    @Query("SELECT cthd FROM ChiTietHoaDon cthd WHERE  cthd.hoaDon.id = :idHD")
    Page<ChiTietHoaDon> findByHoaDon_Id(@Param("idHD") String idHD, Pageable pageable);
    @Query("SELECT cthd FROM ChiTietHoaDon cthd WHERE cthd.hoaDon.id=:idHD AND cthd.chiTietSanPham.id=:idCTSP")
    ChiTietHoaDon trungCTHD(@Param("idHD") String idHD,@Param("idCTSP") String idCTSP);
}
