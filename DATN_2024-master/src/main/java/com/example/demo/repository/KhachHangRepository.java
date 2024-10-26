package com.example.demo.repository;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.Quyen;
import com.example.demo.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang,String> {
//    @Query("SELECT k.ma FROM KhachHang k ORDER BY k.id DESC LIMIT 1")
//    String findLastCustomerCode();
    boolean existsByMa(String ma);
    KhachHang getById(String id);
    List<KhachHang> findByTenContainingIgnoreCase(String ten);

    @Query("SELECT k FROM KhachHang k WHERE k.ten = :ten")
    List<KhachHang> getKhachHangByTen(@Param("ten") String ten);

}
