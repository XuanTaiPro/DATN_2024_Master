package com.example.demo.repository;

import com.example.demo.entity.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, String> {
        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma")
        ChiTietSanPham getByMa(@Param("ma") String ma);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id=:idSP")
        Page<ChiTietSanPham> getAllByIdSP(@Param("idSP") String idSP, Pageable pageable);
        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id = :idSP AND ctsp.trangThai = :trangThai")
        List<ChiTietSanPham> getAllByIdSPHD(@Param("idSP") String idSP, @Param("trangThai") int trangThai);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma AND ctsp.id<>:id")
        ChiTietSanPham getByMaAndId(@Param("ma") String ma, @Param("id") String id);

        @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id = :idSP " +
                        "AND ctsp.soNgaySuDung = :soNgaySuDung " )
        ChiTietSanPham trungCTSP(@Param("idSP") String idSP,
                        @Param("soNgaySuDung") String soNgaySuDung
                                 );

}
