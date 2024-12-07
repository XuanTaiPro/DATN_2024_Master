package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.chitietsanpham.ChiTietSanPhamRequest;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.LoHang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.ChiTietSanPhamRepository;
import com.example.demo.repository.LoHangRepository;
import com.example.demo.repository.SanPhamRepository;

@Service
public class ChiTietSanPhamService {

    @Autowired
    SanPhamRepository sanPhamRepository;

    @Autowired
    LoHangRepository lHRepo;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    GenerateCodeAll genMa;

    public String validateRequest(ChiTietSanPhamRequest request, LocalDateTime dateNow) {
        if (request.getHsd() == null) {
            return "Hạn sử dụng không để trống";
        }
        if (!request.getHsd().isAfter(dateNow)) {
            return "Hạn sử dụng phải sau hôm nay";
        }
        if (request.getNsx() == null) {
            return "Ngày sản xuất không để trống";
        }
        if (!request.getNsx().isBefore(dateNow)) {
            return "Ngày sản xuất phải trước ngày hiện tại";
        }
        return null;
    }

    public void updateExistingProduct(ChiTietSanPham existingProduct, ChiTietSanPhamRequest request) {

        List<LoHang> loForCTSP = lHRepo.fByIdCTSP(existingProduct.getId());
        if (loForCTSP.isEmpty()) {
            createNewLoHang(existingProduct, request);
        } else {
            for (LoHang lH : loForCTSP) {
                boolean checkHSD = lH.getHsd().truncatedTo(ChronoUnit.DAYS)
                        .isEqual(request.getHsd().truncatedTo(ChronoUnit.DAYS));
                boolean checkNSX = lH.getNsx().truncatedTo(ChronoUnit.DAYS)
                        .isEqual(request.getNsx().truncatedTo(ChronoUnit.DAYS));

                if (checkHSD && checkNSX) {
                    lH.setSoLuong(lH.getSoLuong() + request.getSoLuong());
                } else {
                    lH.setSoLuong(request.getSoLuong());
                }
                lHRepo.save(lH);
            }
        }
    }

    public ChiTietSanPham createNewProduct(String generatedMa, ChiTietSanPhamRequest request) {
        ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
        BeanUtils.copyProperties(request, chiTietSanPham);
        chiTietSanPham.setMa(generatedMa);
        chiTietSanPham.setNgayTao(LocalDateTime.now());
        chiTietSanPham.setNgaySua(null);

        if (request.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(request.getIdSP()).orElse(null);
            if (sanPham == null) {
                throw new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + request.getIdSP());
            }
            chiTietSanPham.setSanPham(sanPham);
        }
        return chiTietSanPham;
    }

    public void createNewLoHang(ChiTietSanPham chiTietSanPham, ChiTietSanPhamRequest request) {
        String genMaLo;
        do {
            genMaLo = genMa.generateMa("LH-", 7);
        } while (lHRepo.findByMa(genMaLo) != null);

        LoHang loHang = new LoHang();
        loHang.setMa(genMaLo);
        loHang.setSoLuong(request.getSoLuong());
        loHang.setNgayNhap(request.getNgayNhap());
        loHang.setHsd(request.getHsd());
        loHang.setNsx(request.getNsx());
        loHang.setTrangThai(1);
        loHang.setCtsp(chiTietSanPham);

        lHRepo.save(loHang);
    }
}
