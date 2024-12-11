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
import com.example.demo.service.ChiTietHoaDonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
    private ChiTietSanPhamRepository chiTietSanPhamRepo;

    @Autowired
    private ChiTietHoaDonService cthdService;

    @Autowired
    private LoHangRepository lHRepo;

    @Autowired
    private LHwithHDrepository lhhdRepo;

    @PostMapping("/checkSoLuong")
    public ResponseEntity<?> checkSoLuong(@RequestBody List<Map<String, Object>> mapCTHD) {

        if (mapCTHD.size() == 0) {
            return ResponseEntity.badRequest().body("Giá trị gửi lên không có");
        }
        for (Map<String, Object> itemCTHD : mapCTHD) {
            String idCTSP = (String) itemCTHD.get("idCTSP");

            Object soLuongObj = itemCTHD.get("soLuong");
            Integer sL;

            if (soLuongObj instanceof Integer) {
                sL = (Integer) soLuongObj;
            } else {
                try {
                    sL = Integer.valueOf((String) soLuongObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }
            }

            int getResult = cthdService.checkSL(idCTSP, sL);
            if (getResult == -1) {
                return ResponseEntity.ok().body("Không có sản phẩm trong lô hàng");
            }
            if (getResult != 0) {
                return ResponseEntity.badRequest().body(getResult);
            }
        }

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/add")
    public ResponseEntity<String> createChiTietHoaDon(@ModelAttribute ChiTietHoaDonReq req) {
        String idHD = req.getIdHD();
        if (idHD == null) {
            return ResponseEntity.badRequest().body("ID hóa đơn không tồn tại.");
        }

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

        List<ChiTietHoaDon> listCTHD = chiTietHoaDonRepo.findByHoaDon(idHD);

        ChiTietHoaDon getCTHD = null;
        for (ChiTietHoaDon cthd : listCTHD) {
            if (cthd.getChiTietSanPham().getId().equals(idCtsp)) {
                getCTHD = cthd;
                break;
            }
        }

        Integer soLuong = req.getSoLuong();
        if (soLuong > cthdService.getTotalSoLuong(idCtsp)) {
            return ResponseEntity.badRequest()
                    .body("Sản phẩm không đủ để cung cấp." + cthdService.getTotalSoLuong(idCtsp));
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

            // update so luong in lo hang
            lo.setSoLuong(lo.getSoLuong() - usedQuantity);
            lHRepo.save(lo);
        }

        GiamGia gg = chiTietSanPham.getSanPham().getGiamGia();
        double gia = Double.parseDouble(chiTietSanPham.getGia());

        double giaSauGiamFinal = gia;

        if (gg != null) {
            double phanTramGiam = Double.parseDouble(gg.getGiaGiam());
            giaSauGiamFinal = Math.max(0, gia - gia * phanTramGiam / 100);
        }

        ChiTietHoaDon ctHoaDon;
        if (getCTHD == null) {
            ctHoaDon = cthdService.creatNewCTHD(hoaDonOptional.get(), chiTietSanPham);
            ctHoaDon.setGiaSauGiam(String.valueOf(giaSauGiamFinal));
            ctHoaDon.setSoLuong(req.getSoLuong());
        } else {
            ctHoaDon = getCTHD;
            ctHoaDon.setSoLuong(ctHoaDon.getSoLuong() + req.getSoLuong());
        }

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
    public ResponseEntity<?> updateCTHDSoLuong(@RequestBody Map<String, Object> req) {
        String id = (String) req.get("id");

        ChiTietHoaDon cTHDExisting = chiTietHoaDonRepo.findById(id).get();

        if (cTHDExisting == null) {
            return ResponseEntity.badRequest().body("Chi tiết hóa đơn không tồn tại");
        }

        String idCTSP = cTHDExisting.getChiTietSanPham().getId();

        Integer sLRequest = (Integer) req.get("soLuong");

        if (sLRequest > cthdService.getTotalSoLuong(idCTSP)) {
            return ResponseEntity.badRequest().body("Sản phẩm không đủ để cung cấp.");
        }

        // check tang hay giam
        boolean isIncrease = sLRequest > cTHDExisting.getSoLuong();
        int quantityDiff = Math.abs(sLRequest - cTHDExisting.getSoLuong());

        if (isIncrease) {
            List<LoHang> listLH = lHRepo.findBySoLuongGreaterThanZero(idCTSP);
            listLH.sort(Comparator.comparing(LoHang::getHsd));
            for (LoHang lh : listLH) {
                if (quantityDiff == 0)
                    break;

                int usedQuantity = Math.min(quantityDiff, lh.getSoLuong());
                quantityDiff -= usedQuantity;

                lh.setSoLuong(lh.getSoLuong() - usedQuantity);

                LoHangWithHoaDon lwhd = lhhdRepo.getByIdLoHang(idCTSP, cTHDExisting.getId());
                if (lwhd == null) {
                    lwhd = new LoHangWithHoaDon();
                    lwhd.setLoHang(lh);
                    lwhd.setCthd(cTHDExisting);
                }
                lwhd.setSoLuong(lwhd.getSoLuong() + usedQuantity);

                lHRepo.save(lh);
                lhhdRepo.save(lwhd);
            }
        } else {
            Sort sort = Sort.by(Sort.Order.desc("loHang.hsd"));
            List<LoHangWithHoaDon> listLH = lhhdRepo.getByIdCTHD(cTHDExisting.getId(), sort);

            for (LoHangWithHoaDon lwh : listLH) {
                if (quantityDiff == 0)
                    break;

                int revertQuantity = Math.min(quantityDiff, lwh.getSoLuong());
                quantityDiff -= revertQuantity;

                lwh.setSoLuong(lwh.getSoLuong() - revertQuantity);

                LoHang lh = lHRepo.findById(lwh.getLoHang().getId()).get();
                lh.setSoLuong(lh.getSoLuong() + revertQuantity);

                lhhdRepo.save(lwh);
                lHRepo.save(lh);
            }
        }

        cTHDExisting.setSoLuong(sLRequest);
        cTHDExisting.setNgaySua(LocalDateTime.now());
        chiTietHoaDonRepo.save(cTHDExisting);

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
