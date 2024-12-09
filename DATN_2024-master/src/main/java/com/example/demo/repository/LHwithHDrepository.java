package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.LoHangWithHoaDon;

public interface LHwithHDrepository extends JpaRepository<LoHangWithHoaDon, String> {

    @Query("select lwh from LoHangWithHoaDon lwh where lwh.cthd.id = :idCTHD")
    List<LoHangWithHoaDon> getByIdCTHD(@Param("idCTHD") String id, Sort sort);

    @Query("select lwh from LoHangWithHoaDon lwh where lwh.loHang.id = :idLo and lwh.cthd.id = :idCTHD")
    LoHangWithHoaDon getByIdLoHang(@Param("idLo") String idLH, @Param("idCTHD") String idCTHD);
}
