package com.example.demo.repository;

import com.example.demo.entity.LoaiVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoaiVoucherRepository extends JpaRepository<LoaiVoucher, String> {
    LoaiVoucher getById(String id);

    boolean existsByMa(String ma);

    List<LoaiVoucher> findByTenContainingIgnoreCase(String ten);

    LoaiVoucher getLoaiVoucherByTen(String ten);
}
