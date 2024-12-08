package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.LoHang;
import com.example.demo.repository.ChiTietHoaDonRepo;
import com.example.demo.repository.LoHangRepository;

@Service
public class ChiTietHoaDonService {
    @Autowired
    private LoHangRepository lHRepo;

    @Autowired
    private GenerateCodeAll genCode;

    @Autowired
    private ChiTietHoaDonRepo cthdRepo;

    public boolean checkSL(String idCTSP, Integer soLuong) {
        List<LoHang> listLo = lHRepo.fByIdCTSP(idCTSP);

        Collections.sort(listLo, Comparator.comparing(lo -> lo.getHsd()));

        if (listLo.get(0).getSoLuong() > soLuong) {
            return false;
        }

        return true;
    }

    public Integer getTotalSoLuong(String idCTSP) {
        List<LoHang> listLo = lHRepo.fByIdCTSP(idCTSP);
        int totalSoLuong = 0;
        for (LoHang lo : listLo) {
            totalSoLuong += lo.getSoLuong();
        }
        return totalSoLuong;
    }

    public String genCodeCTHD() {
        String generatedMa;
        do {
            generatedMa = genCode.generateMa("CTHD-", 5);
        } while (cthdRepo.findByMa(generatedMa) != null);
        return generatedMa;
    }

    public ChiTietHoaDon creatNewCTHD(HoaDon hd, ChiTietSanPham ctsp) {
        ChiTietHoaDon cthd = new ChiTietHoaDon();

        cthd.setMaCTHD(genCodeCTHD());
        cthd.setChiTietSanPham(ctsp);
        cthd.setHoaDon(hd);
        cthd.setNgayTao(LocalDateTime.now());
        cthd.setTrangThai(1);

        return cthd;
    }
}
