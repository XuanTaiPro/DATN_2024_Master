package com.example.demo.controller;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.example.demo.dto.hoadon.HoaDonRep;
import com.example.demo.dto.hoadon.HoaDonReq;
import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.ChiTietHoaDonRepo;
import com.example.demo.repository.HoaDonRepo;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NhanVienRepository;
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

    @GetMapping("/page")
    public ResponseEntity<?> page(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                  @RequestParam(name = "trangThai", required = false) Integer trangThai,
                                  @RequestParam(name = "searchText", required = false) String tenKH,
                                  @RequestParam(name = "loaiHD", required = false) Integer loaiHD,
                                  @RequestParam(name = "nhanVien", required = false) String nhanVien) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<HoaDon> hoaDonPage = hoaDonRepo.findHDByFilters(trangThai,  loaiHD, nhanVien, pageRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("hoaDons", hoaDonPage.getContent().stream().map(HoaDon::toResponse).collect(Collectors.toList()));
        response.put("totalPages", hoaDonPage.getTotalPages());
        response.put("totalElements", hoaDonPage.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getHDTaiQuay")
    public ResponseEntity<?> page() {
        List<HoaDon> listHoaDon = hoaDonRepo.getHDTaiQuay(1);
        return ResponseEntity.ok(listHoaDon);
    }
    @GetMapping("/getHDNullKH")
    public ResponseEntity<?> nullKH() {
        List<HoaDon> listHoaDon = hoaDonRepo.getHDNullKH();
        return ResponseEntity.ok(listHoaDon);
    }
    @PostMapping("/add")
    public ResponseEntity<?> createHoaDon(@ModelAttribute HoaDonReq req) {
        // Kiểm tra ID nhân viên
        if (req.getIdNV() == null || req.getIdNV().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID nhân viên không được để trống.");
        }
        // Tạo mới hóa đơn
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

//        // Xử lý khách hàng
//        if (req.getIdKH() != null && !req.getIdKH().trim().isEmpty()) {
//            Optional<KhachHang> khachHangOptional = khachHangRepo.findById(req.getIdKH());
//            if (khachHangOptional.isPresent()) {
//                hoaDon.setKhachHang(khachHangOptional.get());
//            } else {
//                return ResponseEntity.badRequest().body("Không tìm thấy khách hàng với ID: " + req.getIdKH());
//            }
//        } else {
//            return ResponseEntity.badRequest().body("ID khách hàng không được để trống.");
//        }

        // Xử lý nhân viên
        Optional<NhanVien> nhanVienOptional = nhanVienRepo.findById(req.getIdNV());
        if (nhanVienOptional.isPresent()) {
            hoaDon.setNhanVien(nhanVienOptional.get());
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy nhân viên với ID: " + req.getIdNV());
        }

        // Trước khi lưu hóa đơn
        System.out.println("Mã hóa đơn: " + hoaDon.getMaHD());
        System.out.println("Ngày tạo: " + hoaDon.getNgayTao());
        System.out.println("Ngày sửa: " + hoaDon.getNgaySua());
        System.out.println("Ngày thanh toán: " + hoaDon.getNgayThanhToan());
        System.out.println("Ngày nhận hàng: " + hoaDon.getNgayNhanHang());
        System.out.println("Mã voucher: " + hoaDon.getMaVoucher());
        System.out.println("Thành tiền: " + hoaDon.getThanhTien());
        System.out.println("Phí vận chuyển: " + hoaDon.getPhiVanChuyen());
        System.out.println("Trạng thái: " + hoaDon.getTrangThai());

        // Lưu hóa đơn
        // Lưu hóa đơn
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
    @PutMapping("/huyHD")
    public ResponseEntity<String> huyHD(@RequestParam(name = "idHD") String idHD) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDonExisting = hoaDonOptional.get();
            hoaDonExisting.setTrangThai(4);
            hoaDonRepo.save(hoaDonExisting);
            return ResponseEntity.ok("Hủy hóa đơn thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại.");
        }
    }
    @GetMapping("/listNV")
    public List<NhanVienResponse> getAllNhanVien() {
        NhanVien nhanVien=new NhanVien();
        nhanVien.setNgayTao(LocalDateTime.now());
        List<NhanVien> nhanVienList = nhanVienRepo.findAll();
        return nhanVienList.stream()
                .map(NhanVien::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/listKH")
    public ResponseEntity<List<KhachHangResponse>> getAllKhachHang() {
        KhachHang khachHang=new KhachHang();
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
        if (hoaDon.isPresent()) {
            HoaDonRep hoaDonRep = hoaDon.get().toResponse(); // Populate DTO
            List<ChiTietHoaDonRep> chiTietResponses = hoaDon.get().getChiTietHoaDons().stream()
                    .map(ChiTietHoaDon::toResponse)
                    .collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("hoaDonRep", hoaDonRep);
            response.put("chiTietHoaDons", chiTietResponses);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại.");
        }
    }


    // Read by ID
//    @GetMapping("/detail/{idHD}")
//    public ResponseEntity<HoaDonRep> getHoaDonById(@PathVariable String idHD) {
//        return hoaDonRepo.findById(idHD).map(hoaDon -> {
//            List<ChiTietHoaDon> chiTietHoaDons = chiTietHoaDonRepo.findByHoaDon_Id(idHD);
//
//            List<ChiTietHoaDonRep> chiTietRepList = chiTietHoaDons.stream().map(chiTiet -> {
//                ChiTietHoaDonRep chiTietRep = new ChiTietHoaDonRep();
//                chiTietRep.setId(chiTiet.getId());
//                chiTietRep.setMaCTHD(chiTiet.getMaCTHD());
//                chiTietRep.setTongTien(chiTiet.getTongTien());
//                chiTietRep.setSoLuong(chiTiet.getSoLuong());
//                chiTietRep.setGiaBan(chiTiet.getGiaBan());
//                chiTietRep.setTrangThai(chiTiet.getTrangThai());
//                chiTietRep.setGhiChu(chiTiet.getGhiChu());
//                return chiTietRep;
//            }).collect(Collectors.toList());
//
//            // Chuyển đổi hoaDon thành DTO và thiết lập danh sách chi tiết
//            HoaDonRep rep = hoaDon.toResponse();
//            return ResponseEntity.ok(rep);
//        }).orElseGet(() -> ResponseEntity.notFound().build());
//    }

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
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
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
            hoaDon.setTenNguoiNhan(req.getTenNguoiNhan());
            hoaDon.setSdtNguoiNhan(req.getSdtNguoiNhan());
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHoaDon(@PathVariable String id) {
        if (hoaDonRepo.existsById(id)) {
            hoaDonRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
