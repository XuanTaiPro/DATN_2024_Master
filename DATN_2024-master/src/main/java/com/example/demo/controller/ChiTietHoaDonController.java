package com.example.demo.controller;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.example.demo.dto.chitiethoadon.ChiTietHoaDonReq;
import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.GiamGia;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.ChiTietHoaDonRepo;
import com.example.demo.repository.ChiTietSanPhamRepository;
import com.example.demo.repository.HoaDonRepo;
import com.example.demo.repository.SanPhamRepository;
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

    @PostMapping("/add")
    public ResponseEntity<String> createChiTietHoaDon(@Validated @ModelAttribute ChiTietHoaDonReq req) {

        // Kiểm tra tồn tại của hóa đơn
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(req.getIdHD());
        if (hoaDonOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
        }

        // Kiểm tra tồn tại của chi tiết sản phẩm
        Optional<ChiTietSanPham> chiTietSanPhamOptional = chiTietSanPhamRepo.findById(req.getIdCTSP());
        if (chiTietSanPhamOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm không tồn tại.");
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamOptional.get();

        // Kiểm tra số lượng sản phẩm đủ để trừ hay không
        if (chiTietSanPham.getSoLuong() < req.getSoLuong()) {
            return ResponseEntity.badRequest().body("Số lượng sản phẩm không đủ.");
        }

        GiamGia getGiamGia = sPRepo.findById(chiTietSanPham.getSanPham().getId()).get().getGiamGia();
        double giaSauGiam = 0;
        if (getGiamGia != null) {
            String phanTramGiam = getGiamGia.getGiaGiam();
            giaSauGiam = Double.parseDouble(chiTietSanPham.getGia())
                    - Double.parseDouble(chiTietSanPham.getGia()) * Double.parseDouble(phanTramGiam) / 100;
        }
        // Tìm ChiTietHoaDon hiện có
        ChiTietHoaDon cTHDExisting = chiTietHoaDonRepo.trungCTHD(req.getIdHD(), req.getIdCTSP());
        if (cTHDExisting != null) {
            // Nếu đã tồn tại, tăng số lượng của ChiTietHoaDon hiện có và trừ số lượng của
            // ChiTietSanPham
            cTHDExisting.setSoLuong(cTHDExisting.getSoLuong() + req.getSoLuong());
            cTHDExisting.setGiaSauGiam(String.valueOf(giaSauGiam));
            chiTietHoaDonRepo.save(cTHDExisting);
        } else {
            // Nếu chưa tồn tại, tạo mới ChiTietHoaDon
            ChiTietHoaDon chiTietHoaDons = new ChiTietHoaDon();
            chiTietHoaDons.setGiaSauGiam(String.valueOf(giaSauGiam));
            chiTietHoaDons.setSoLuong(req.getSoLuong());
            chiTietHoaDons.setTrangThai(1);
            chiTietHoaDons.setNgayTao(LocalDateTime.now());
            chiTietHoaDons.setGhiChu(req.getGhiChu());
            chiTietHoaDons.setHoaDon(hoaDonOptional.get());
            chiTietHoaDons.setChiTietSanPham(chiTietSanPham);
            chiTietHoaDons.setGiaBan(req.getGiaBan());
            chiTietHoaDonRepo.save(chiTietHoaDons);
        }

        // Trừ số lượng sản phẩm
        chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - req.getSoLuong());
        chiTietSanPhamRepo.save(chiTietSanPham);

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
        ChiTietHoaDon chiTietHoaDonExisting = chiTietHoaDonRepo.getById(id);

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
            chiTietHoaDonExisting.setGiaSauGiam("0");
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

    // @PutMapping("/update")
    // public ResponseEntity<?> updateChiTietHoaDon(@RequestParam(name = "id")
    // String id,
    // @Validated @ModelAttribute ChiTietHoaDonReq req) {
    // Optional<ChiTietHoaDon> chiTietHoaDonOptional =
    // chiTietHoaDonRepo.findById(id);
    // if (chiTietHoaDonOptional.isEmpty()) {
    // return ResponseEntity.status(404).body("Không tìm thấy chi tiết hóa đơn với
    // ID: " + id);
    // }
    // ChiTietHoaDon chiTietHoaDon = chiTietHoaDonOptional.get();
    // <<<<<<< HEAD
    // double giaBan = Double.parseDouble(req.getGiaBan());
    // int soLuong = req.getSoLuong();
    //
    // =======
    //
    // ChiTietSanPham ctSP = chiTietSanPhamRepo.findById(req.getIdCTSP()).get();
    // SanPham sp = sPRepo.findById(ctSP.getSanPham().getId()).get();
    // GiamGia giamGia = sp.getGiamGia();
    //
    // int giaSauGiam = 0;
    //
    // if (giamGia != null) {
    // if (Integer.parseInt(giamGia.getGiaGiam()) < 100) {
    // giaSauGiam = Integer.parseInt(ctSP.getGia())
    // - Integer.parseInt(ctSP.getGia()) * Integer.parseInt(giamGia.getGiaGiam()) /
    // 100;
    // }
    // }
    //
    // chiTietHoaDon.setGiaSauGiam(String.valueOf(giaSauGiam));
    // >>>>>>> 3748b3f12bafbb8e0a2715e0eef64972cf0b4aab
    //
    // chiTietHoaDon.setSoLuong(req.getSoLuong());
    // chiTietHoaDon.setGiaBan(req.getGiaBan());
    // chiTietHoaDon.setNgaySua(LocalDateTime.now());
    //
    // Optional<ChiTietSanPham> chiTietSanPhamOptional =
    // chiTietSanPhamRepo.findById(req.getIdCTSP());
    // if (chiTietSanPhamOptional.isPresent()) {
    // chiTietHoaDon.setChiTietSanPham(chiTietSanPhamOptional.get());
    // } else {
    // return ResponseEntity.badRequest().body("Chi tiết sản phẩm không tồn tại.");
    // }
    //
    // Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(req.getIdHD());
    // if (hoaDonOptional.isPresent()) {
    // chiTietHoaDon.setHoaDon(hoaDonOptional.get());
    // } else {
    // return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
    // }
    // System.out.println("Yêu cầu cập nhật:" + req);
    // chiTietHoaDonRepo.save(chiTietHoaDon);
    // return ResponseEntity.ok("Cập nhật chi tiết hóa đơn thành công.");
    // }

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
