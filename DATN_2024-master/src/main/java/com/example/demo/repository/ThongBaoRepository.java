package com.example.demo.repository;

import com.example.demo.entity.ThongBao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThongBaoRepository extends JpaRepository<ThongBao, String> {
    boolean existsByMa(String ma);

    @Query("SELECT tb FROM ThongBao tb WHERE (:noiDung IS NULL OR :noiDung = '' OR tb.noiDung LIKE %:noiDung%) AND " +
            "(:trangThai IS NULL OR tb.trangThai = :trangThai)")
    Page<ThongBao> searchTB(
            @Param("noiDung") String noiDung,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}
