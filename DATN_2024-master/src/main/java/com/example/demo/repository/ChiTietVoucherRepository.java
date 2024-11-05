package com.example.demo.repository;

import com.example.demo.entity.ChiTietVoucher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietVoucherRepository extends JpaRepository<ChiTietVoucher ,String> {
    List<ChiTietVoucher> findByKhachHang_Id(String idKH);

//    List<ChiTietVoucher> findByVoucher_Id(String voucherId);

    @Modifying
    @Transactional
    @Query("delete  From ChiTietVoucher ctvc where ctvc.voucher.id = :voucherID")
    void deleteByVoucherId(@Param("voucherID") String voucherID);

    @Query("select ctvc.id from ChiTietVoucher  ctvc  where ctvc.voucher.id = :voucherID")
    List<String> getCTVC(@Param("voucherID") String voucherID);

    @Query("SELECT c.khachHang.id FROM ChiTietVoucher c WHERE c.voucher.id = :voucherId")
    List<String> findCustomerIdsByVoucherId(@Param("voucherId") String voucherId);
}

