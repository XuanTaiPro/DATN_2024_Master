package com.example.demo.repository;

import com.example.demo.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, String> {
    boolean existsByMa(String ma);

    @Query("SELECT g FROM GioHangChiTiet g ORDER BY g.ngayTao DESC")
    List<GioHangChiTiet> findAllOrderByNgayTaoDesc();

    @Query("Select g from GioHangChiTiet g where g.khachHang.id = :idkh")
    List<GioHangChiTiet> getAllByKhach(@Param(value = "idkh") String id);

    @Query("select g from GioHangChiTiet g where g.khachHang.id = :idKh and g.chiTietSanPham.id = :idCTSP")
    GioHangChiTiet getByIdKhachAndCTSP(@Param("idKh") String idKh, @Param("idCTSP") String idCTSP);
}
