package com.example.demo.repository;

import com.example.demo.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
    boolean existsByMa(String ma);

    List<Voucher> findByTenContainingIgnoreCase(String ten);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:ten IS NULL OR v.ten LIKE %:ten%) AND " +
            "(:giamGia IS NULL OR v.giamGia LIKE CONCAT('%',:giamGia ,'%') ) AND " +
            "(:trangThai IS NULL OR v.trangThai = :trangThai)")
    Page<Voucher> filterVouchers(
            @Param("ten") String ten,
            @Param("giamGia") String giamGia,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);

    List<Voucher> findByTrangThaiAndSoLuongGreaterThan(Integer trangThai, int soLuong);
}
