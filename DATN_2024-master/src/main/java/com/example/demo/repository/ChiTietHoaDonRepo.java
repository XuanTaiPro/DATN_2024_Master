package com.example.demo.repository;

import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.ChiTietSanPham;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietHoaDonRepo extends JpaRepository<ChiTietHoaDon, String> {
    @Query("SELECT cthd FROM ChiTietHoaDon cthd WHERE  cthd.hoaDon.id = :idHD")
    Page<ChiTietHoaDon> findByHoaDon_Id(@Param("idHD") String idHD, Pageable pageable);

    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id=:idSP")
    List<ChiTietSanPham> getAllByIdSP(@Param("idSP") String idSP);
}
