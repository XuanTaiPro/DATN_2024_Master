package com.example.demo.repository;

import com.example.demo.entity.ChiTietVoucher;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietVoucherRepository extends JpaRepository<ChiTietVoucher, String> {

    @Query("select c from ChiTietVoucher c where c.khachHang.id = :idKH and c.voucher.trangThai = 1")
    Page<ChiTietVoucher> findByIdKH(String idKH, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete  From ChiTietVoucher ctvc where ctvc.voucher.id = :voucherID")
    void deleteByVoucherId(@Param("voucherID") String voucherID);

    @Query("select ctvc.id from ChiTietVoucher ctvc  where ctvc.voucher.id = :voucherID")
    List<String> getCTVC(@Param("voucherID") String voucherID);

    @Query("SELECT c.khachHang.id FROM ChiTietVoucher c WHERE c.voucher.id = :voucherId")
    List<String> findCustomerIdsByVoucherId(@Param("voucherId") String voucherId);

    @Query("select c.id from ChiTietVoucher c where c.khachHang.id = :idkh and c.trangThai = 1")
    List<String> getByIdKhach(String idkh);

    @Query("select c from ChiTietVoucher c where c.voucher.id = :idVC and c.khachHang.id = :idKh")
    ChiTietVoucher getByIdVCAndIdKh(@Param("idVC") String idVC, @Param("idKh") String idKh);
}
