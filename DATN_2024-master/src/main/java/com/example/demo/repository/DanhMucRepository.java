package com.example.demo.repository;

import com.example.demo.entity.DanhMuc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DanhMucRepository extends JpaRepository<DanhMuc, String> {
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ma=:ma")
    DanhMuc getByMa(@Param("ma") String ma);

    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ten=:ten")
    DanhMuc getByName(@Param("ten") String ten);

    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ma=:ma AND dm.id<>:id")
    DanhMuc getByMaAndId(@Param("ma") String ma, @Param("id") String id);

    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ten=:ten AND dm.id<>:id")
    DanhMuc getByNameAndId(@Param("id") String id, @Param("ten") String ten);

    @Query("SELECT DISTINCT dm " +
            "FROM DanhMuc dm " +
            "JOIN dm.sanPham sp " +
            "JOIN sp.listCTSP ctp " +
            "JOIN ctp.loHang lh " +
            "WHERE lh.soLuong > 0")
    List<DanhMuc> findDanhMucWithValidProducts();
}
