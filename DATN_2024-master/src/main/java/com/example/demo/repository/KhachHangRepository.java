package com.example.demo.repository;

import com.example.demo.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    // @Query("SELECT k.ma FROM KhachHang k ORDER BY k.id DESC LIMIT 1")
    // String findLastCustomerCode();
    boolean existsByMa(String ma);
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
    boolean existsByEmailAndIdNot(String email, String id);
    boolean existsBySdtAndIdNot(String sdt, String id);

    KhachHang getById(String id);

    @Query("SELECT kh FROM KhachHang  kh WHERE " +
            "(:ten IS NULL OR kh.ten LIKE %:ten%) AND " +
            "(:gioiTinh IS NULL OR :gioiTinh = '' OR kh.gioiTinh = :gioiTinh) AND " +
            "(:diaChi IS NULL OR :diaChi = '' OR kh.diaChi LIKE %:diaChi%) AND " +
            "(:trangThai IS NULL OR kh.trangThai = :trangThai)")
    Page<KhachHang> timKiemVaLocKhachHang(
            @Param("ten") String ten,
            @Param("gioiTinh") String gioiTinh,
            @Param("diaChi") String diaChi,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);

    @Query("SELECT kh FROM KhachHang kh WHERE " +
            "(:ten IS NULL OR kh.ten LIKE %:ten%) AND" +
            "(:gioiTinh IS NULL OR :gioiTinh = '' OR kh.gioiTinh = :gioiTinh ) AND " +
            "(:sdt IS NULL OR :sdt = '' OR kh.sdt LIKE %:sdt%) "
           )
    List<KhachHang> tkVaLocKhachHang(
            @Param("ten") String ten,
            @Param("gioiTinh") String gioiTinh,
            @Param("sdt") String sdt
    );

    @Query("SELECT kh FROM KhachHang kh WHERE kh.trangThai = 1")
    List<KhachHang> getKHTrue();

    @Query("SELECT kh FROM KhachHang kh WHERE kh.email = :email")
    KhachHang getKhachHangByEmail(String email);

    KhachHang getKhachHangByTen(String ten);

    @Query("SELECT kh FROM KhachHang kh WHERE kh.email = :email AND kh.passw = :passw ")
    KhachHang loginKH(@Param("email") String email, @Param("passw") String passw);

    @Query("SELECT kh FROM KhachHang  kh WHERE kh.ma =:ma")
    KhachHang getByMa(@Param("ma") String ma);

}
