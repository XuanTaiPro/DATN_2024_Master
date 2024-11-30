package com.example.demo.controller;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.example.demo.dto.hoadon.HoaDonRep;
import com.example.demo.dto.hoadon.HoaDonReq;
import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.GenerateCodeAll;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private GenerateCodeAll generateCodeAll;

    @Autowired
    private GiamGiaRepository ggRepo;

    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private ChiTietHoaDonRepo chiTietHoaDonRepo;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepo;

    @Autowired
    private ThongTinGiaoHangRepository ttghRepo;

    @Autowired
    private VoucherRepository vcRepo;

    @Autowired
    private ChiTietVoucherRepository ctvcRepo;

    @Autowired
    private DanhGiaRepository dgRepo;

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
                "totalElements", hoaDonPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getHDTaiQuay")
    public ResponseEntity<?> page() {
        List<HoaDon> listHoaDon = hoaDonRepo.getHDTaiQuay(1);
        return ResponseEntity.ok(listHoaDon.stream().map(HoaDon::toResponse));
    }

    @GetMapping("/tuDongXoaHoaDon")
    @Transactional
    public ResponseEntity<?> tuDongXoaHD() {
        // Lấy tất cả hóa đơn cần xử lý
        List<HoaDon> listHoaDon = hoaDonRepo.getHDTaiQuay(1);

        // Duyệt qua từng hóa đơn
        for (HoaDon hoaDon : listHoaDon) {
            // Tính toán số ngày giữa thời điểm hiện tại và ngày tạo hóa đơn
            long daysBetween = ChronoUnit.DAYS.between(hoaDon.getNgayTao(), LocalDateTime.now());

            // Nếu hóa đơn đã được tạo hơn 1 ngày
            if (daysBetween >= 1) {
                // Lấy danh sách chi tiết hóa đơn trước khi xóa hóa đơn
                List<ChiTietHoaDon> listCTHD = chiTietHoaDonRepo.getByIdHD(hoaDon.getId());

                // Cập nhật số lượng sản phẩm trước khi xóa chi tiết hóa đơn
                for (ChiTietHoaDon cthd : listCTHD) {
                    ChiTietSanPham chiTietSanPham = cthd.getChiTietSanPham();
                    // Cập nhật lại số lượng sản phẩm
                    chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + cthd.getSoLuong());
                    chiTietSanPhamRepo.save(chiTietSanPham);
                }

                // Xóa tất cả các chi tiết hóa đơn
                chiTietHoaDonRepo.deleteAll(listCTHD);

                // Sau khi cập nhật xong, xóa hóa đơn
                hoaDonRepo.delete(hoaDon);
            }
        }

        // Trả về phản hồi khi xóa thành công
        return ResponseEntity.ok("Đã xóa các hóa đơn cũ");
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

    @PostMapping("/add-hD-online")
    public ResponseEntity<?> addHDOnline(@RequestBody Map<String, Object> payMap) {
        String idKh = (String) payMap.get("idKH");
        Map<String, String> inforKh;
        String idAddress, discountCode;

        List<Map<String, Object>> payList = (List<Map<String, Object>>) payMap.get("listProduct");
        String payment = (String) payMap.get("payment");

        HoaDon hd = new HoaDon();
        hd.setMaHD(generateCodeAll.generateMa("HD-", 7));
        hd.setNgayTao(LocalDateTime.now());
        hd.setNgaySua(null);
        hd.setTrangThai(0);
        hd.setLoaiHD(0);
        hd.setThanhtoan(Integer.parseInt(payment));

        if (Integer.parseInt(payment) == 2) {
            hd.setNgayThanhToan(LocalDateTime.now());
        }

        if (idKh == null) {
            inforKh = (Map<String, String>) payMap.get("inforNoLogin");

            String tenNN = inforKh.get("nameNoLogin");
            String sdtNN = inforKh.get("phoneNoLogin");
            String dcNN = inforKh.get("addressNoLogin");

            if ("".equals(tenNN.trim())) {
                return ResponseEntity.badRequest().body("Tên người nhận không được để trống.");
            }

            if ("".equals(sdtNN.trim())) {
                return ResponseEntity.badRequest().body("Số điện thoại người nhận không được để trống.");
            } else if (!sdtNN.matches("\\d+")) {
                return ResponseEntity.badRequest().body("Số điện thoại người nhận chỉ bao gồm số");
            }

            if ("".equals(dcNN.trim())) {
                return ResponseEntity.badRequest().body("Địa chỉ người nhận không được để trống.");
            }

            hd.setTenNguoiNhan(tenNN);
            hd.setSdtNguoiNhan(sdtNN);
            hd.setDiaChiNguoiNhan(dcNN);

            hd.setKhachHang(null);
        } else {
            KhachHang kh = khachHangRepo.findById(idKh).orElse(null);

            if (kh == null) {
                return ResponseEntity.badRequest().body("Khách hàng không được tìm thấy");
            }

            hd.setKhachHang(kh);

            idAddress = (String) payMap.get("indexAddress");

            if (Integer.parseInt(idAddress) == 0) {
                // người đặt chọn thông tin giao hàng mặc định
                hd.setTenNguoiNhan(kh.getTen());
                hd.setSdtNguoiNhan(kh.getSdt());
                hd.setDiaChiNguoiNhan(kh.getDiaChi());
            } else {
                // set địa chỉ người nhận
                List<ThongTinGiaoHang> listTTGH = ttghRepo.fHangs(kh.getId());

                if (listTTGH.get(Integer.parseInt(idAddress) - 1) == null) {
                    return ResponseEntity.badRequest().body("Giá trị của thông tin giao hàng không tồn tại");
                }

                hd.setTenNguoiNhan(listTTGH.get(Integer.parseInt(idAddress) - 1).getTenNguoiNhan());
                hd.setSdtNguoiNhan(listTTGH.get(Integer.parseInt(idAddress) - 1).getSdtNguoiNhan());
                hd.setDiaChiNguoiNhan(listTTGH.get(Integer.parseInt(idAddress) - 1).getDcNguoiNhan());
            }

            discountCode = (String) payMap.get("discountCode");
            if ("Chưa có".equals(discountCode)) {
                hd.setMaVoucher(null);
            } else {
                Voucher voucher = vcRepo.findById(discountCode).orElse(null);

                if (voucher == null) {
                    return ResponseEntity.badRequest().body("Voucher Code sai");
                }

                ChiTietVoucher ctvc = ctvcRepo.getByIdVCAndIdKh(voucher.getId(), idKh);
                ctvc.setTrangThai(2);

                ctvcRepo.save(ctvc);

                hd.setMaVoucher(discountCode);
            }
        }

        hoaDonRepo.save(hd);

        for (Map<String, Object> prod : payList) {
            ChiTietHoaDon cthd = new ChiTietHoaDon();

            HoaDon getHD = hoaDonRepo.findByMaHD(hd.getMaHD()).get();
            if (getHD == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi tìm hóa đơn");
            }

            cthd.setHoaDon(getHD);
            Integer soLuongTrongGio = (Integer) prod.get("soLuongTrongGio");

            cthd.setSoLuong(soLuongTrongGio);
            cthd.setNgayTao(LocalDateTime.now());

            String idCTSP = (String) prod.get("id");
            ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(idCTSP).orElse(null);

            if (ctsp == null) {
                return ResponseEntity.badRequest().body("Sản phẩm gửi đi không tồn tại");
            }

            cthd.setGiaBan(ctsp.getGia());
            cthd.setChiTietSanPham(ctsp);

            String idSP = (String) prod.get("idSP");
            String idGiamGia = ggRepo.getIdGiamGia(idSP);
            if (idGiamGia != null) {
                GiamGia gg = ggRepo.findById(idGiamGia).orElse(null);

                if (gg == null) {
                    return ResponseEntity.badRequest().body("Giảm giá gửi đi không tồn tại");
                }

                Integer giaSauGiam = Integer.parseInt(ctsp.getGia()) - Integer.parseInt(ctsp.getGia())
                        * Integer.parseInt(gg.getGiaGiam()) / 100;

                cthd.setGiaSauGiam(String.valueOf(giaSauGiam));
            } else {
                cthd.setGiaSauGiam(ctsp.getGia());
            }

            chiTietHoaDonRepo.save(cthd);
        }

        return ResponseEntity.ok().body("Thêm thành công");
    }

    @PutMapping("/xacNhanHD")
    public ResponseEntity<String> xacNhanHD(@RequestParam(name = "idHD") String idHD) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDonExisting = hoaDonOptional.get();
            hoaDonExisting.setTrangThai(2);
            hoaDonRepo.save(hoaDonExisting);

            KhachHang getKH = hoaDonExisting.getKhachHang();

            List<ChiTietHoaDon> cthdList = chiTietHoaDonRepo.findByHoaDon(hoaDonExisting.getId());
            for (ChiTietHoaDon cthd : cthdList) {
                DanhGia dg = new DanhGia();
                ChiTietSanPham getCTSP = chiTietSanPhamRepo.findById(cthd.getChiTietSanPham().getId()).get();
                dg.setChiTietSanPham(getCTSP);

                dg.setKhachHang(getKH);
                dg.setNgayDanhGia(LocalDateTime.now());
                dg.setTrangThai(0);

                dgRepo.save(dg);
            }

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
            hoaDon.setNgayThanhToan(req.getNgayThanhToan());
            hoaDon.setNgayNhanHang(req.getNgayNhanHang());
            hoaDon.setTrangThai(req.getTrangThai());
            hoaDon.setLoaiHD(req.getLoaiHD());
            hoaDon.setDiaChiNguoiNhan(req.getDiaChiNguoiNhan());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setGhiChu(req.getGhiChu());

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

    @PutMapping("/update-ghi-chu/{id}")
    public ResponseEntity<Void> updateGhiChu(@PathVariable String id, @RequestBody Map<String, String> payload) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Lấy giá trị ghi chú từ payload và cập nhật
            String ghiChu = payload.get("ghiChu");
            if (ghiChu != null) {
                hoaDon.setGhiChu(ghiChu);
                hoaDon.setNgaySua(LocalDateTime.now()); // Cập nhật thời gian sửa
            }

            try {
                hoaDonRepo.save(hoaDon);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
