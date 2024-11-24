package com.example.demo.controller;

import com.example.demo.dto.hoadon.HoaDonReq;
import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("hoadon")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    private NhanVienRepository nhanVienRepo;
    @Autowired
    private KhachHangRepository khachHangRepo;
    @Autowired
    private ChiTietHoaDonRepo chiTietHoaDonRepo;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepo;
    @GetMapping("/page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Integer loaiHD,
            @RequestParam(required = false) String nhanVien) {

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<HoaDon> hoaDonPage = (searchText != null)
                ? hoaDonRepo.findHDByFilters(trangThai, loaiHD, nhanVien, searchText, pageRequest)
                : hoaDonRepo.findHDByFiltersNullTenKH(trangThai, loaiHD, nhanVien, pageRequest);

        Map<String, Object> response = Map.of(
                "hoaDons", hoaDonPage.getContent().stream().map(HoaDon::toResponse).collect(Collectors.toList()),
                "totalPages", hoaDonPage.getTotalPages(),
                "totalElements", hoaDonPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getHDTaiQuay")
    public ResponseEntity<?> page() {
        List<HoaDon> listHoaDon = hoaDonRepo.getHDTaiQuay(1);
        return ResponseEntity.ok(listHoaDon.stream().map(HoaDon::toResponse));
    }

    @GetMapping("/getHDNullKH")
    public ResponseEntity<?> nullKH() {
        List<HoaDon> listHoaDon = hoaDonRepo.getHDNullKH();
        return ResponseEntity.ok(listHoaDon);
    }

    @GetMapping("getHDbyClientID")
    public ResponseEntity<?> getHDbyClientID(@RequestParam String idKH) {
        if (hoaDonRepo.getHDByCustomerId(idKH) == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy hóa đơn nào cho khách hàng này");
        } else {
            return ResponseEntity.ok(hoaDonRepo.getHDByCustomerId(idKH).stream().map(HoaDon::toResponse));
        }
    }

    @GetMapping("getHDbyClientSDT")
    public ResponseEntity<?> getHDbyClientSDT(@RequestParam String sdt) {
        if (sdt.trim().isEmpty())
            return ResponseEntity.badRequest().body("Số điện thoại đang trống");

        if (hoaDonRepo.getHDBySDT(sdt) == null || hoaDonRepo.getHDBySDT(sdt).size() == 0) {
            return ResponseEntity.badRequest().body("Không tìm thấy hóa đơn nào cho số điện " + sdt);
        } else {
            return ResponseEntity.ok(hoaDonRepo.getHDBySDT(sdt).stream().map(HoaDon::toResponse));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> createHoaDon(@ModelAttribute HoaDonReq req) {
        if (req.getIdNV() == null || req.getIdNV().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID nhân viên không được để trống.");
        }
        HoaDon hoaDon = new HoaDon();

        // Sao chép các thuộc tính khác từ req
        BeanUtils.copyProperties(req, hoaDon);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(null);
        hoaDon.setTrangThai(1);
        hoaDon.setLoaiHD(0);
        hoaDon.setMaVoucher(null); // Không gán giá trị cho maVoucher
        hoaDon.setNgayThanhToan(null); // Không gán giá trị cho ngayThanhToan
        hoaDon.setNgayNhanHang(null); // Không gán giá trị cho ngayNhanHang
        hoaDon.setThanhTien(null);
        hoaDon.setPhiVanChuyen(null);
        hoaDon.setKhachHang(null);
        hoaDon.setSdtNguoiNhan(null);

        // Xử lý nhân viên
        Optional<NhanVien> nhanVienOptional = nhanVienRepo.findById(req.getIdNV());
        if (nhanVienOptional.isPresent()) {
            hoaDon.setNhanVien(nhanVienOptional.get());
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy nhân viên với ID: " + req.getIdNV());
        }
        try {
            hoaDonRepo.save(hoaDon);
            return ResponseEntity.ok().body(Map.of("message", "Thêm hóa đơn thành công!", "hoaDon", hoaDon));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lưu hóa đơn: " + e.getMessage()));
        }

    }

    @PutMapping("/xacNhanHD")
    public ResponseEntity<String> xacNhanHD(@RequestParam(name = "idHD") String idHD) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDonExisting = hoaDonOptional.get();
            hoaDonExisting.setTrangThai(2);
            hoaDonRepo.save(hoaDonExisting);
            return ResponseEntity.ok("Xác nhận hóa đơn thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại.");
        }
    }

    @GetMapping("/listNV")
    public List<NhanVienResponse> getAllNhanVien() {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setNgayTao(LocalDateTime.now());
        List<NhanVien> nhanVienList = nhanVienRepo.findAll();
        return nhanVienList.stream()
                .map(NhanVien::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/listKH")
    public ResponseEntity<List<KhachHangResponse>> getAllKhachHang() {
        KhachHang khachHang = new KhachHang();
        khachHang.setNgayTao(LocalDateTime.now());
        List<KhachHang> khachHangList = khachHangRepo.findAll();
        List<KhachHangResponse> responseList = khachHangList.stream()
                .map(KhachHang::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detailHoaDon(@RequestParam(name = "idHD") String idHD) {
        Optional<HoaDon> hoaDon = hoaDonRepo.findById(idHD);
        if (hoaDon != null) {
            return ResponseEntity.ok(hoaDon.get().toResponse());
        } else {
            return ResponseEntity.badRequest().body("Không tìm được hóa đơn");
        }
    }
    // Read by ID
    // @GetMapping("/detail/{idHD}")
    // public ResponseEntity<HoaDonRep> getHoaDonById(@PathVariable String idHD) {
    // return hoaDonRepo.findById(idHD).map(hoaDon -> {
    // List<ChiTietHoaDon> chiTietHoaDons = chiTietHoaDonRepo.findByHoaDon_Id(idHD);
    //
    // List<ChiTietHoaDonRep> chiTietRepList = chiTietHoaDons.stream().map(chiTiet
    // -> {
    // ChiTietHoaDonRep chiTietRep = new ChiTietHoaDonRep();
    // chiTietRep.setId(chiTiet.getId());
    // chiTietRep.setMaCTHD(chiTiet.getMaCTHD());
    // chiTietRep.setTongTien(chiTiet.getTongTien());
    // chiTietRep.setSoLuong(chiTiet.getSoLuong());
    // chiTietRep.setGiaBan(chiTiet.getGiaBan());
    // chiTietRep.setTrangThai(chiTiet.getTrangThai());
    // chiTietRep.setGhiChu(chiTiet.getGhiChu());
    // return chiTietRep;
    // }).collect(Collectors.toList());
    //
    // // Chuyển đổi hoaDon thành DTO và thiết lập danh sách chi tiết
    // HoaDonRep rep = hoaDon.toResponse();
    // return ResponseEntity.ok(rep);
    // }).orElseGet(() -> ResponseEntity.notFound().build());
    // }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateHoaDon(@PathVariable String id, @Validated @RequestBody HoaDonReq req) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Chỉ cập nhật idNV nếu không null
            if (req.getIdNV() != null && !req.getIdNV().trim().isEmpty()) {
                Optional<NhanVien> nhanVienOptional = nhanVienRepo.findById(req.getIdNV());
                if (nhanVienOptional.isPresent()) {
                    hoaDon.setNhanVien(nhanVienOptional.get());
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            }

            // Chỉ cập nhật idKH nếu không null
            if (req.getIdKH() != null && !req.getIdKH().trim().isEmpty()) {
                Optional<KhachHang> khachHangOptional = khachHangRepo.findById(req.getIdKH());
                if (khachHangOptional.isPresent()) {
                    hoaDon.setKhachHang(khachHangOptional.get());
                    hoaDon.setTenNguoiNhan(null);
                    hoaDon.setSdtNguoiNhan(null);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } else {
                hoaDon.setKhachHang(null);
                hoaDon.setTenNguoiNhan(req.getTenNguoiNhan());
                hoaDon.setSdtNguoiNhan(req.getSdtNguoiNhan());
            }
            // Cập nhật các thông tin khác
            hoaDon.setMaHD(req.getMaHD());
            hoaDon.setMaVoucher(req.getMaVoucher());
            hoaDon.setThanhTien(req.getThanhTien());
            hoaDon.setNgayThanhToan(req.getNgayThanhToan());
            hoaDon.setNgayNhanHang(req.getNgayNhanHang());
            hoaDon.setTrangThai(req.getTrangThai());
            hoaDon.setLoaiHD(req.getLoaiHD());
            hoaDon.setPhiVanChuyen(req.getPhiVanChuyen());
            hoaDon.setDiaChiNguoiNhan(req.getDiaChiNguoiNhan());
            hoaDon.setNgaySua(LocalDateTime.now());

            try {
                hoaDonRepo.save(hoaDon);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                // Xử lý lỗi lưu vào cơ sở dữ liệu
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteHoaDon(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        if (!hoaDonRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<ChiTietHoaDon> listCTHD = chiTietHoaDonRepo.getByIdHD(id);
        for (ChiTietHoaDon cthd : listCTHD) {
            ChiTietSanPham chiTietSanPham = cthd.getChiTietSanPham();
            // Cập nhật lại số lượng sản phẩm
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + cthd.getSoLuong());
            chiTietSanPhamRepo.save(chiTietSanPham);
        }

        // Xóa tất cả các chi tiết hóa đơn
        chiTietHoaDonRepo.deleteAll(listCTHD);

        // Xóa hóa đơn chính
        hoaDonRepo.deleteById(id);

        // Trả về mã 200 nếu thành công
        return ResponseEntity.ok().build();
    }

}
