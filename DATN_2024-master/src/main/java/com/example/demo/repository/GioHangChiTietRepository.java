package com.example.demo.repository;

import com.example.demo.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet ,String> {
    boolean existsByMa(String ma);

    @Query("SELECT g FROM GioHangChiTiet g ORDER BY g.ngayTao DESC")
    List<GioHangChiTiet> findAllOrderByNgayTaoDesc();
}
