package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.LoHang;

@Repository
public interface LoHangRepository extends JpaRepository<LoHang, String> {

    @Query("select l from LoHang l where l.ctsp.id = :idCTSP")
    List<LoHang> fByIdCTSP(@Param("idCTSP") String idCTSP);

    @Query("SELECT l FROM LoHang l WHERE l.ma = :ma")
    LoHang findByMa(@Param("ma") String ma);

    @Query("Select l from LoHang l where l.ctsp.id = :idCTSP and l.soLuong > 0")
    List<LoHang> findBySoLuongGreaterThanZero(@Param("idCTSP") String idCTSP);
}
