package com.example.demo.controller;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.example.demo.dto.chitiethoadon.ChiTietHoaDonReq;
import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.HoaDon;
import com.example.demo.repository.ChiTietHoaDonRepo;
import com.example.demo.repository.ChiTietSanPhamRepository;
import com.example.demo.repository.HoaDonRepo;
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
    private ChiTietSanPhamRepository chiTietSanPhamRepo;

    @PostMapping("/add")
    public ResponseEntity<String> createChiTietHoaDon(@Validated @RequestBody ChiTietHoaDonReq req) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(req.getIdHD());
        if (hoaDonOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
        }

        Optional<ChiTietSanPham> chiTietSanPhamOptional = chiTietSanPhamRepo.findById(req.getIdCTSP());
        if (chiTietSanPhamOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm không tồn tại.");
        }

        ChiTietHoaDon chiTietHoaDons = new ChiTietHoaDon();
        chiTietHoaDons.setMaCTHD(req.getMaCTHD());
        chiTietHoaDons.setTongTien(req.getTongTien());

        chiTietHoaDons.setTrangThai(req.getTrangThai());
        chiTietHoaDons.setNgayTao(LocalDateTime.now());
        chiTietHoaDons.setGhiChu(req.getGhiChu());
        chiTietHoaDons.setHoaDon(hoaDonOptional.get());
        chiTietHoaDons.setChiTietSanPham(chiTietSanPhamOptional.get());

        chiTietHoaDonRepo.save(chiTietHoaDons);
        return ResponseEntity.ok("Thêm chi tiết hóa đơn thành công.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChiTietHoaDonRep>> getAllChiTietHoaDon() {
        List<ChiTietHoaDonRep> chiTietHoaDons = chiTietHoaDonRepo.findAll().stream()
                .map(ChiTietHoaDon::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chiTietHoaDons);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateChiTietHoaDon(@PathVariable String id, @Validated @RequestBody ChiTietHoaDonReq req) {
        Optional<ChiTietHoaDon> chiTietHoaDonOptional = chiTietHoaDonRepo.findById(id);
        if (chiTietHoaDonOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy chi tiết hóa đơn với ID: " + id);
        }

        ChiTietHoaDon chiTietHoaDon = chiTietHoaDonOptional.get();


        double giaBan = Double.parseDouble(req.getGiaBan());
        int soLuong = req.getSoLuong();
        double tongTien = giaBan * soLuong;

        chiTietHoaDon.setTongTien(String.valueOf(Double.valueOf(tongTien)));
        chiTietHoaDon.setSoLuong(req.getSoLuong());
        chiTietHoaDon.setGiaBan(req.getGiaBan());
        chiTietHoaDon.setNgaySua(LocalDateTime.now());
        chiTietHoaDon.setGhiChu(req.getGhiChu());


        Optional<ChiTietSanPham> chiTietSanPhamOptional = chiTietSanPhamRepo.findById(req.getIdCTSP());
        if (chiTietSanPhamOptional.isPresent()) {
            chiTietHoaDon.setChiTietSanPham(chiTietSanPhamOptional.get());
        } else {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm không tồn tại.");
        }

        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(req.getIdHD());
        if (hoaDonOptional.isPresent()) {
            chiTietHoaDon.setHoaDon(hoaDonOptional.get());
        } else {
            return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
        }
        System.out.println("Yêu cầu cập nhật:"+ req);
        chiTietHoaDonRepo.save(chiTietHoaDon);
        return ResponseEntity.ok("Cập nhật chi tiết hóa đơn thành công.");
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
        response.put("cthds", cthdPage.getContent().stream().map(ChiTietHoaDon::toResponse).collect(Collectors.toList()));
        response.put("totalPages", cthdPage.getTotalPages());
        response.put("totalElements", cthdPage.getTotalElements());

        // Trả về kết quả dưới dạng ResponseEntity
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteChiTietHoaDon(@PathVariable String id) {
        if (chiTietHoaDonRepo.existsById(id)) {
            chiTietHoaDonRepo.deleteById(id);
            return ResponseEntity.ok("Xóa chi tiết hóa đơn thành công.");
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy chi tiết hóa đơn với ID: " + id);
        }
    }
}
