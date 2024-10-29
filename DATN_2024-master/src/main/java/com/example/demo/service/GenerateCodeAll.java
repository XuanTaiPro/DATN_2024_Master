package com.example.demo.service;

import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GenerateCodeAll {

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private NhanVienRepository nvRepo;

    @Autowired
    private ThongBaoRepository tbRepo;

    @Autowired
    private VoucherRepository vcRepo;

    @Autowired
    private LoaiVoucherRepository lvcRepo;

    @Autowired
    private GioHangChiTietRepository ghctRepo;

    public String generateMa(String ma, int length) {
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, length);
        return ma + randomPart.toUpperCase();
    }

    public String generateMaKhachHang() {
        String ma;
        do {
            ma = generateMa("KH-", 5);
        } while (khRepo.existsByMa(ma));
        return ma;
    }

    public String generateMaNhanVien() {
        String ma;
        do {
            ma = generateMa("NV-", 5);
        } while (nvRepo.existsByMa(ma));
        return ma;
    }

    public String generateMaThongBao() {
        String ma;
        do {
            ma = generateMa("TB-", 5);
        } while (tbRepo.existsByMa(ma));
        return ma;
    }

    public String generateMaVoucher() {
        String ma;
        do {
            ma = generateMa("VC-", 5);
        } while (vcRepo.existsByMa(ma));
        return ma;
    }

    public String generateMaChiTietVoucher() {
        String ma;
        do {
            ma = generateMa("CTVC-", 5);
        } while (vcRepo.existsByMa(ma));
        return ma;
    }

    public String generateMaGHCT() {
        String ma;
        do {
            ma = generateMa("GHCT-", 5);
        } while (ghctRepo.existsByMa(ma));
        return ma;
    }

    public String generateMaLVC() {
        String ma;
        do {
            ma = generateMa("LVC-", 5);
        } while (lvcRepo.existsByMa(ma));
        return ma;
    }

}
