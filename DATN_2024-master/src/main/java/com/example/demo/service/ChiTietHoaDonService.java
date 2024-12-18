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

    public int checkSL(String idCTSP, Integer soLuong) {
        List<LoHang> listLo = lHRepo.fByIdCTSP(idCTSP);

        if (listLo.size() == 0) {
            return -1;
        }

        Collections.sort(listLo, Comparator.comparing(lo -> lo.getHsd()));

        if (soLuong > listLo.get(0).getSoLuong()) {
            return listLo.get(0).getSoLuong();
        }

        return 0;
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
        cthd.setGiaBan(ctsp.getGia());
        cthd.setNgayTao(LocalDateTime.now());
        cthd.setTrangThai(1);

        return cthd;
    }
}
