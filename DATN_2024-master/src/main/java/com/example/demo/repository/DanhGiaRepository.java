package com.example.demo.repository;

import com.example.demo.entity.DanhGia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DanhGiaRepository extends JpaRepository<DanhGia, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM DanhGia dg WHERE dg.chiTietSanPham.id = :idCTSP")
    void deleteByIdCTSP(@Param("idCTSP") String idCTSP);

    @Query("SELECT AVG(dg.soSao) FROM DanhGia dg WHERE dg.chiTietSanPham.id = :idCTSP")
    Double getAvgRatingByIdCTSP(@Param("idCTSP") String idCTSP);

    @Query("select d from DanhGia d where d.chiTietSanPham.id = :idCTSP and d.khachHang.id = :idKH")
    DanhGia getDGbySPandKH(@Param("idCTSP") String idCTSP, @Param("idKH") String idKH);

    @Query("SELECT d FROM DanhGia d WHERE d.khachHang.id = :idKH")
    List<DanhGia> getByIdKH(@Param("idKH") String idkh);
}
