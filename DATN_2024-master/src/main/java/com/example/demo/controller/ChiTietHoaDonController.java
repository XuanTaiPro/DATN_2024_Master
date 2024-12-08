package com.example.demo.controller;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.example.demo.dto.chitiethoadon.ChiTietHoaDonReq;
import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.GiamGia;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.LoHang;
import com.example.demo.entity.LoHangWithHoaDon;
import com.example.demo.repository.ChiTietHoaDonRepo;
import com.example.demo.repository.ChiTietSanPhamRepository;
import com.example.demo.repository.HoaDonRepo;
import com.example.demo.repository.LHwithHDrepository;
import com.example.demo.repository.LoHangRepository;
import com.example.demo.repository.SanPhamRepository;
import com.example.demo.service.ChiTietHoaDonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("chitiethoadon")
public class ChiTietHoaDonController {

    @Autowired
    private ChiTietHoaDonRepo chiTietHoaDonRepo;

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private SanPhamRepository sPRepo;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepo;

    @Autowired
    private ChiTietHoaDonService cthdService;

    @Autowired
    private LoHangRepository lHRepo;

    @Autowired
    private LHwithHDrepository lhhdRepo;

    @PostMapping("/checkSoLuong")
    public ResponseEntity<?> checkSoLuong(@RequestBody Map<String, Object> mapCTHD) {

        String idCTSP = (String) mapCTHD.get("idCTSP");

        Object soLuongObj = mapCTHD.get("soLuong");
        Integer sL;

        if (soLuongObj instanceof Integer) {
            sL = (Integer) soLuongObj;
        } else {
            try {
                sL = Integer.valueOf((String) mapCTHD.get("soLuong"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        }

        if (!cthdService.checkSL(idCTSP, sL)) {
            // status = 204
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/add")
    public ResponseEntity<String> createChiTietHoaDon(@Validated @ModelAttribute ChiTietHoaDonReq req) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(req.getIdHD());
        if (hoaDonOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
        }

        Optional<ChiTietSanPham> chiTietSanPhamOptional = chiTietSanPhamRepo.findById(req.getIdCTSP());
        if (chiTietSanPhamOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm không tồn tại.");
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamOptional.get();

        String idCtsp = chiTietSanPham.getId();

        Integer soLuong = req.getSoLuong();
        if (soLuong > cthdService.getTotalSoLuong(idCtsp)) {
            return ResponseEntity.badRequest().body("Sản phẩm không đủ để cung cấp.");
        }

        List<LoHang> listLo = lHRepo.fByIdCTSP(idCtsp);
        listLo.sort(Comparator.comparing(LoHang::getHsd));

        List<LoHangWithHoaDon> listLH = new ArrayList<>();

        for (LoHang lo : listLo) {
            if (soLuong <= 0)
                break;

            int usedQuantity = Math.min(soLuong, lo.getSoLuong());
            soLuong -= usedQuantity;

            LoHangWithHoaDon lhhd = new LoHangWithHoaDon();
            lhhd.setSoLuong(usedQuantity);
            lhhd.setLoHang(lo);
            listLH.add(lhhd);
        }

        GiamGia gg = chiTietSanPham.getSanPham().getGiamGia();
        double gia = Double.parseDouble(chiTietSanPham.getGia());

        double giaSauGiamFinal = gia;

        if (gg != null) {
            double phanTramGiam = Double.parseDouble(gg.getGiaGiam());
            giaSauGiamFinal = Math.max(0, gia - gia * phanTramGiam / 100);
        }

        ChiTietHoaDon ctHoaDon = cthdService.creatNewCTHD(hoaDonOptional.get(), chiTietSanPham);
        ctHoaDon.setGiaSauGiam(String.valueOf(giaSauGiamFinal));
        ctHoaDon.setGiaBan(chiTietSanPham.getGia());

        chiTietHoaDonRepo.save(ctHoaDon);

        for (LoHangWithHoaDon lhhd : listLH) {
            lhhd.setCthd(ctHoaDon);
            lhhdRepo.save(lhhd);
        }


        return ResponseEntity.ok("Thêm chi tiết hóa đơn thành công.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChiTietHoaDonRep>> getAllChiTietHoaDon() {
        List<ChiTietHoaDonRep> chiTietHoaDons = chiTietHoaDonRepo.findAll().stream()
                .map(ChiTietHoaDon::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chiTietHoaDons);
    }

    @PutMapping("/updateSoLuong")
    public ResponseEntity<?> updateCTHDSoLuong(@RequestParam(name = "id") String id,
            @Validated @ModelAttribute ChiTietHoaDon req) {
        ChiTietHoaDon chiTietHoaDonExisting = chiTietHoaDonRepo.findById(id).get();

        if (chiTietHoaDonExisting == null) {
            return ResponseEntity.badRequest().body("Chi tiết hóa đơn không tồn tại");
        }
        int oldSoLuong = chiTietHoaDonExisting.getSoLuong();
        int newSoLuong = req.getSoLuong();
        int soLuongDifference = newSoLuong - oldSoLuong;

        // Cập nhật ChiTietSanPham liên kết với ChiTietHoaDon
        ChiTietSanPham chiTietSanPham = chiTietHoaDonExisting.getChiTietSanPham();
        if (chiTietSanPham != null) {
            int updatedSoLuongCTSP = chiTietSanPham.getSoLuong() - soLuongDifference;

            if (updatedSoLuongCTSP < 0) {
                return ResponseEntity.badRequest().body("Không đủ số lượng trong kho");
            }

            chiTietSanPham.setSoLuong(updatedSoLuongCTSP);
            chiTietSanPhamRepo.save(chiTietSanPham);
        }

        GiamGia getGiamGia = sPRepo.findById(chiTietSanPham.getSanPham().getId()).get().getGiamGia();
        double giaSauGiam = 0;
        if (getGiamGia != null) {
            String phanTramGiam = getGiamGia.getGiaGiam();
            int phanTram = Integer.parseInt(phanTramGiam);

            if (phanTram < 100) {
                giaSauGiam = Double.parseDouble(chiTietSanPham.getGia())
                        - Double.parseDouble(chiTietSanPham.getGia()) * Double.parseDouble(phanTramGiam) / 100;

                chiTietHoaDonExisting.setGiaSauGiam(String.valueOf(giaSauGiam));
            }
        } else {
            chiTietHoaDonExisting.setGiaSauGiam(chiTietSanPham.getGia());
        }

        chiTietHoaDonExisting.setGiaBan(chiTietSanPham.getGia());
        chiTietHoaDonExisting.setSoLuong(req.getSoLuong());
        chiTietHoaDonExisting.setHoaDon(chiTietHoaDonExisting.getHoaDon());
        chiTietHoaDonExisting.setChiTietSanPham(chiTietHoaDonExisting.getChiTietSanPham());
        chiTietHoaDonExisting.setMaCTHD(chiTietHoaDonExisting.getMaCTHD());
        chiTietHoaDonExisting.setNgayTao(chiTietHoaDonExisting.getNgayTao());
        chiTietHoaDonExisting.setNgaySua(LocalDateTime.now());
        chiTietHoaDonRepo.save(chiTietHoaDonExisting); // Save the updated entity

        return ResponseEntity.ok("Update Done!");
    }

    @GetMapping("/getCTHD")
    public ResponseEntity<?> getChiTietHoaDonByIdHD(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "idHD") String idHD) {

        // Kiểm tra nếu idHD không hợp lệ (ví dụ: null hoặc trống)
        if (idHD == null || idHD.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã hóa đơn không hợp lệ.");
        }

        // Tạo đối tượng PageRequest để phân trang
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "ngayTao"));

        // Lấy danh sách chi tiết hóa đơn theo idHD và phân trang
        Page<ChiTietHoaDon> cthdPage = chiTietHoaDonRepo.findByHoaDon_Id(idHD, pageRequest);

        // Tạo response trả về
        Map<String, Object> response = new HashMap<>();
        response.put("cthds",
                cthdPage.getContent().stream().map(ChiTietHoaDon::toResponse).collect(Collectors.toList()));
        response.put("totalPages", cthdPage.getTotalPages());
        response.put("totalElements", cthdPage.getTotalElements());

        // Trả về kết quả dưới dạng ResponseEntity
        return ResponseEntity.ok(response);
    }

    @GetMapping("getAllByOrderId")
    public ResponseEntity<?> getAllByOrderId(@RequestParam(name = "idHD") String idHD) {
        if (chiTietHoaDonRepo.findByHoaDon(idHD) == null) {
            return ResponseEntity.badRequest().body("Không tìm được chi tiết cho hóa đơn");
        } else {
            return ResponseEntity.ok(chiTietHoaDonRepo.findByHoaDon(idHD).stream().map(ChiTietHoaDon::toResponse));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteChiTietHoaDon(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        ChiTietHoaDon existingCTHD = chiTietHoaDonRepo.findById(id).get();
        if (chiTietHoaDonRepo.existsById(id)) {
            existingCTHD.getChiTietSanPham()
                    .setSoLuong(existingCTHD.getSoLuong() + existingCTHD.getChiTietSanPham().getSoLuong());
            chiTietSanPhamRepo.save(existingCTHD.getChiTietSanPham());
            chiTietHoaDonRepo.deleteById(id);
            return ResponseEntity.ok("Xóa chi tiết hóa đơn thành công.");
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy chi tiết hóa đơn với ID: " + id);
        }
    }
}
