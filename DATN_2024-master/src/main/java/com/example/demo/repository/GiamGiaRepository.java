package com.example.demo.repository;

import com.example.demo.entity.GiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface GiamGiaRepository extends JpaRepository<GiamGia, String> {
    @Query("SELECT gg FROM GiamGia gg where gg.ma=:ma")
    GiamGia getByMa(@Param("ma") String ma);

    @Query("SELECT gg FROM GiamGia gg WHERE gg.ten = :ten " +
            "AND gg.id<>:id " +
            "AND " +
            "((:ngayBatDau >= gg.ngayBatDau AND :ngayBatDau <= gg.ngayKetThuc) " +
            "OR (:ngayKetThuc >= gg.ngayBatDau AND :ngayKetThuc <= gg.ngayKetThuc) " +
            "OR (gg.ngayBatDau >= :ngayBatDau AND gg.ngayBatDau <= :ngayKetThuc))")
    GiamGia getByNameAndTimeOverlap(@Param("ten") String ten,
                                    @Param("ngayBatDau") LocalDateTime ngayBatDau,
                                    @Param("ngayKetThuc") LocalDateTime ngayKetThuc,
                                    @Param("id") String id);

    @Query("SELECT gg FROM GiamGia gg where gg.ma=:ma and gg.id<>:id")
    GiamGia getByMaAndId(@Param("ma") String ma, @Param("id") String id);

    @Query(value = "Select idgiamgia from sanpham where id = :id", nativeQuery = true)
    String getIdGiamGia(@Param("id") String id);
    @Query("SELECT g FROM GiamGia g WHERE " +
            "(:searchText IS NULL OR LOWER(g.ten) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
            "(:trangThai IS NULL OR g.trangThai = :trangThai) AND " +
            "(:ngayBatDau IS NULL OR g.ngayBatDau >= :ngayBatDau) AND " +
            "(:ngayKetThuc IS NULL OR g.ngayKetThuc <= :ngayKetThuc)")
    Page<GiamGia> filterGiamGia(
            @Param("searchText") String searchText,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") LocalDateTime ngayBatDau,
            @Param("ngayKetThuc") LocalDateTime ngayKetThuc,
            Pageable pageable);
}
