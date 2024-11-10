package com.example.demo.repository;

import com.example.demo.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    // @Query("SELECT k.ma FROM KhachHang k ORDER BY k.id DESC LIMIT 1")
    // String findLastCustomerCode();
    boolean existsByMa(String ma);

    KhachHang getById(String id);

    List<KhachHang> findByTenContainingIgnoreCase(String ten);

    KhachHang getKhachHangByTen(String ten);

}
