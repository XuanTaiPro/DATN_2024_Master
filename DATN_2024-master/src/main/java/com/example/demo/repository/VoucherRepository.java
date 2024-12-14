package com.example.demo.repository;

import com.example.demo.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
    boolean existsByMa(String ma);

    List<Voucher> findByTenContainingIgnoreCase(String ten);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:giamMin IS NULL OR v.giamMin >= :giamMin) AND " +
            "(:giamMax IS NULL OR v.giamMax <= :giamMax) AND " +
            "(:ngayKetThuc IS NULL OR v.ngayKetThuc = :ngayKetThuc)")
    List<Voucher> filterVouchers(@Param("giamMin") String giamMin,
                                 @Param("giamMax") String giamMax,
                                 @Param("ngayKetThuc") String ngayKetThuc);
    @Query("SELECT v from Voucher v WHERE v.ma=:ma")
    Voucher getBYMa(@Param("ma")String ma);

    List<Voucher> findByTrangThaiAndSoLuongGreaterThan(Integer trangThai, int soLuong);
}
