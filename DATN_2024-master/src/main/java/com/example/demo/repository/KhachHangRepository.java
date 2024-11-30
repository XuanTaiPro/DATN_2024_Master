package com.example.demo.repository;

import com.example.demo.entity.*;
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

    KhachHang getKhachHangByTen(String ten);

    @Query("SELECT kh FROM KhachHang kh WHERE kh.email = :email AND kh.passw = :passw ")
    KhachHang loginKH(@Param("email") String email , @Param("passw") String passw);
    @Query("SELECT kh FROM KhachHang  kh WHERE kh.ma =:ma")
    KhachHang getByMa(@Param("ma") String ma);


}
