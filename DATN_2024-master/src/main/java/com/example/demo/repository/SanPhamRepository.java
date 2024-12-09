package com.example.demo.repository;

import com.example.demo.dto.sanpham.SanPhamOnlineResponse;
import com.example.demo.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, String> {
        @Query("SELECT sp FROM SanPham  sp where sp.maSP=:ma")
        SanPham getByMa(@Param("ma") String ma);

        @Query("SELECT sp FROM SanPham sp WHERE sp.tenSP = :tenSP")
        SanPham getByName(@Param("tenSP") String tenSP);

        @Query("SELECT sp FROM SanPham sp WHERE (:tenSP IS NULL OR sp.tenSP LIKE %:tenSP%) AND sp.trangThai=:trangThai")
        List<SanPham> getByTenSP(@Param("tenSP") String tenSP, @Param("trangThai") Integer trangThai);

        @Query("SELECT sp FROM SanPham  sp where sp.maSP=:ma and sp.id<>:id")
        SanPham getByMaAndId(@Param("ma") String ma, @Param("id") String id);

        @Query("SELECT sp FROM SanPham sp WHERE sp.tenSP = :ten AND sp.id <> :id")
        SanPham getByNameAndId(@Param("ten") String ten, @Param("id") String id);

        @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.id=:idDanhMuc")
        List<SanPham> getByIdDanhMuc(@Param("idDanhMuc") String idDanhMuc, Sort sort);

        @Query("SELECT sp FROM SanPham sp WHERE sp.giamGia.id=:id")
        List<SanPham> findByGiamGiaId(@Param("id") String id);

        @Query("SELECT new com.example.demo.dto.sanpham.SanPhamOnlineResponse(" +
                        "sp.id, sp.tenSP, ctp.gia, gg.giaGiam, gg.ngayBatDau, gg.ngayKetThuc, ac.link) " +
                        "FROM SanPham sp " +
                        "LEFT JOIN sp.giamGia gg " +
                        "LEFT JOIN sp.listCTSP ctp " +
                        "LEFT JOIN ctp.anhCTSP ac " +
                        "WHERE ctp.gia = (SELECT MIN(ctp2.gia) FROM ChiTietSanPham ctp2 " +
                        "                 WHERE ctp2.sanPham.id = sp.id " +
                        "                 AND EXISTS (SELECT 1 FROM LoHang lh WHERE lh.ctsp.id = ctp2.id AND lh.soLuong > 0))")
        Page<SanPhamOnlineResponse> findSanPhamWithMinPrice(Pageable pageable);

        @Query("SELECT sp FROM SanPham sp WHERE " +
                        "(:searchText IS NULL OR (sp.tenSP like %:searchText% OR sp.thanhPhan like %:searchText% OR sp.congDung like %:searchText%)) "
                        +
                        "AND (:tenDanhMuc IS NULL OR sp.danhMuc.ten like %:tenDanhMuc%) " +
                        "AND (:trangThai IS NULL OR sp.trangThai = :trangThai) " +
                        "AND (:tuoiMin IS NULL OR sp.tuoiMin >= :tuoiMin) " +
                        "AND (:tuoiMax IS NULL OR sp.tuoiMax <= :tuoiMax)")
        Page<SanPham> filterSanPham(@Param("searchText") String searchText,
                        @Param("tenDanhMuc") String tenDanhMuc,
                        @Param("trangThai") Integer trangThai,
                        @Param("tuoiMax") Integer tuoiMax,
                        @Param("tuoiMin") Integer tuoiMin,
                        Pageable pageable);

    @Query("SELECT new com.example.demo.dto.sanpham.SanPhamOnlineResponse(" +
            "sp.id, sp.tenSP, ctp.gia, gg.giaGiam, gg.ngayBatDau, gg.ngayKetThuc, ac.link) " +
            "FROM SanPham sp " +
            "LEFT JOIN sp.giamGia gg " +
            "LEFT JOIN sp.listCTSP ctp " +
            "LEFT JOIN ctp.anhCTSP ac " +
            "WHERE CAST(ctp.gia AS double) = (SELECT MIN(CAST(ctp2.gia AS double)) FROM ChiTietSanPham ctp2 WHERE ctp2.sanPham.id = sp.id) " +
            "AND (:giaMin IS NULL OR CAST(ctp.gia AS double) >= :giaMin) " +
            "AND (:giaMax IS NULL OR CAST(ctp.gia AS double) <= :giaMax) " +
            "AND (:searchText IS NULL OR " +
            "(sp.tenSP LIKE %:searchText% OR sp.thanhPhan LIKE %:searchText% OR sp.congDung LIKE %:searchText%)) " +
            "AND (:tuoiMin IS NULL OR sp.tuoiMin >= :tuoiMin) " +
            "AND (:tuoiMax IS NULL OR sp.tuoiMax <= :tuoiMax) " +
            "AND (:danhMuc IS NULL OR sp.danhMuc.ten IN :danhMuc)")
    Page<SanPhamOnlineResponse> findSanPhamOnline(@Param("giaMin") Double giaMin,
                                                  @Param("giaMax") Double giaMax,
                                                  @Param("searchText") String searchText,
                                                  @Param("tuoiMin") Integer tuoiMin,
                                                  @Param("tuoiMax") Integer tuoiMax,
                                                  @Param("danhMuc") List<String> danhMuc,
                                                  Pageable pageable);
}
