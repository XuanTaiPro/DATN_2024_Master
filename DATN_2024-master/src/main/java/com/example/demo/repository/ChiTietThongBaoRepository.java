package com.example.demo.repository;

import com.example.demo.entity.ChiTietThongBao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietThongBaoRepository extends JpaRepository<ChiTietThongBao ,String> {

    @Query("SELECT cttb.khachHang.id FROM ChiTietThongBao cttb WHERE cttb.thongBao.id  =:thongBaoID")
    List<String> findKHBythongBaoId(@Param("thongBaoID") String thongBaoID);

    @Query("SELECT cttb.id FROM ChiTietThongBao  cttb WHERE cttb.thongBao.id =:thongBaoID ")
    List<String> getCTTB (@Param("thongBaoID") String thongBaoID);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietThongBao cttb WHERE cttb.thongBao.id = :thongBaoID")
    void deleteByThongBaoID(@Param("thongBaoID") String thongBaoID);

}
