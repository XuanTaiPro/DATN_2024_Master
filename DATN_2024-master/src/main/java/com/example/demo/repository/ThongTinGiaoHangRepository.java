package com.example.demo.repository;

import com.example.demo.entity.ThongTinGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThongTinGiaoHangRepository extends JpaRepository<ThongTinGiaoHang, String> {
        boolean existsByKhachHang_IdAndDcNguoiNhan(String khachHangId, String dcNguoiNhan);

        @Query("SELECT t FROM ThongTinGiaoHang t WHERE " +
                        "(:tenNguoiNhan IS NULL OR t.tenNguoiNhan LIKE %:tenNguoiNhan%) OR " +
                        "(:sdtNguoiNhan IS NULL OR t.sdtNguoiNhan = :sdtNguoiNhan)")
        List<ThongTinGiaoHang> searchByTenOrSdtNguoiNhan(@Param("tenNguoiNhan") String tenNguoiNhan,
                        @Param("sdtNguoiNhan") String sdtNguoiNhan);

        @Query("Select t from ThongTinGiaoHang t where t.khachHang.id = :id and t.trangThai = 1")
        List<ThongTinGiaoHang> fHangs(@Param(value = "id") String id);

        @Query("Select t from ThongTinGiaoHang t where id = :id")
        ThongTinGiaoHang getTTGHById(@Param(value = "id") String id);

        @Query("SELECT t FROM ThongTinGiaoHang t WHERE t.trangThai = 1")
        List<ThongTinGiaoHang> getTTGH();

        @Query("Select t.khachHang.id from ThongTinGiaoHang t where t.id = :id")
        String getCustomerIdByTTGHId(@Param(value = "id") String id);
}
