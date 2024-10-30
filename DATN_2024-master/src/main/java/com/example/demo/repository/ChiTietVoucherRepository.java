package com.example.demo.repository;

import com.example.demo.entity.ChiTietVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietVoucherRepository extends JpaRepository<ChiTietVoucher ,String> {
    List<ChiTietVoucher> findByKhachHang_Id(String idKH);
}
