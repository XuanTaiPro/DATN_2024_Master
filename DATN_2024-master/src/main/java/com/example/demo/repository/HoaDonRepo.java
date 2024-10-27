package com.example.demo.repository;

import com.example.demo.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface HoaDonRepo extends JpaRepository<HoaDon,String> {
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :maHD")
    Optional<HoaDon> findByMaHD(@Param("maHD") String maHD);
}
