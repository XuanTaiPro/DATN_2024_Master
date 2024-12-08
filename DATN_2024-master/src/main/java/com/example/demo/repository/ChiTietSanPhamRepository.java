package com.example.demo.repository;

import com.example.demo.entity.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, String> {
        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma")
        ChiTietSanPham getByMa(@Param("ma") String ma);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id=:idSP")
        Page<ChiTietSanPham> getAllByIdSP(@Param("idSP") String idSP, Pageable pageable);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
                "JOIN FETCH ctsp.listLoHang lohang " +
                "WHERE ctsp.sanPham.id = :idSP " +
                "AND ctsp.trangThai = :trangThai " +
                "AND lohang.soLuong > 0")
        List<ChiTietSanPham> getAllByIdSPHD(@Param("idSP") String idSP, @Param("trangThai") int trangThai);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma AND ctsp.id<>:id")
        ChiTietSanPham getByMaAndId(@Param("ma") String ma, @Param("id") String id);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id = :idSP " +
                        "AND ctsp.soNgaySuDung = :soNgaySuDung ")
        ChiTietSanPham trungCTSP(@Param("idSP") String idSP,
                        @Param("soNgaySuDung") String soNgaySuDung);
        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id = :idSP " +
                "AND (:giaMin IS NULL OR CAST(ctsp.gia AS double) >= :giaMin) " +
                "AND (:giaMax IS NULL OR CAST(ctsp.gia AS double) <= :giaMax) " +
                "AND (:trangThai IS NULL OR ctsp.trangThai = :trangThai)")
        Page<ChiTietSanPham> filterCTSP(@Param("idSP") String idSP,
                                        @Param("giaMin") Double giaMin,
                                        @Param("giaMax") Double giaMax,
                                        @Param("trangThai") Integer trangThai,
                                        Pageable pageable);
        @Query("SELECT DISTINCT ctsp.soNgaySuDung FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id = :idSP")
        List<String> findUniqueSoNgaySuDung(@Param("idSP") String idSP);
}
