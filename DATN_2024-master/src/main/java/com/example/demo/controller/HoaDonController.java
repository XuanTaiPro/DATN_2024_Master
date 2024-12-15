package com.example.demo.controller;

import com.example.demo.dto.chitiethoadon.ChiTietHoaDonRep;
import com.example.demo.dto.hoadon.HoaDonRep;
import com.example.demo.dto.hoadon.HoaDonReq;
import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.ChiTietHoaDonService;
import com.example.demo.service.EmailService;
import com.example.demo.service.GenerateCodeAll;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("hoadon")
public class HoaDonController {
    @Autowired
    private LHwithHDrepository lhhdRepo;

    @Autowired
    private LoHangRepository lHRepo;

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

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChiTietHoaDonService cthdService;
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

        System.out.println(LoginController.tenQuyen);
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
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Đã xóa các hóa đơn cũ");
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
        HoaDon hoaDon = new HoaDon();

        // Sao chép các thuộc tính khác từ req
        BeanUtils.copyProperties(req, hoaDon);
        hoaDon.setMaHD(generateCodeAll.generateMa("HD-", 7));
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(null);
        hoaDon.setTrangThai(1);
        hoaDon.setLoaiHD(0);
        hoaDon.setVoucher(null); // Không gán giá trị cho maVoucher
        hoaDon.setNgayThanhToan(null); // Không gán giá trị cho ngayThanhToan
        hoaDon.setNgayNhanHang(null); // Không gán giá trị cho ngayNhanHang
        hoaDon.setKhachHang(null);
        hoaDon.setSdtNguoiNhan(null);

        // Xử lý nhân viên
        // Optional<NhanVien> nhanVienOptional =
        // nhanVienRepo.findById(loginController.returnIDNV());
        // if (nhanVienOptional.isPresent()) {
        // hoaDon.setNhanVien(nhanVienOptional.get());
        // } else {
        // return ResponseEntity.badRequest().body("Không tìm thấy nhân viên với ID: " +
        // loginController.returnIDNV());
        // }
        Optional<NhanVien> nhanVienOptional = nhanVienRepo.findById(req.getIdNV());
        // Optional<NhanVien> nhanVienOptional = nhanVienRepo.findById("C1ED6E69");
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
        String idKh = (String) payMap.get("idkh");
        Map<String, String> inforKh;
        String idAddress, discountCode;

        List<Map<String, Object>> payList = (List<Map<String, Object>>) payMap.get("listProduct");
        String payment = (String) payMap.get("payment");

        NhanVien nv;

        List<NhanVien> listNV = nhanVienRepo.findAdmin();
        if (listNV.size() == 0) {
            nv = nhanVienRepo.findAll().get(0);
        } else {
            nv = listNV.get(0);
        }

        HoaDon hd = new HoaDon();
        hd.setMaHD(generateCodeAll.generateMa("HD-", 7));
        hd.setNgayTao(LocalDateTime.now());
        hd.setNgaySua(null);
        hd.setTrangThai(0);
        hd.setNhanVien(nv);
        hd.setLoaiHD(1);
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

            if (idAddress == null) {
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
                hd.setVoucher(null);
            } else {
                Voucher voucher = vcRepo.findById(discountCode).orElse(null);

                if (voucher == null) {
                    return ResponseEntity.badRequest().body("Voucher Code sai");
                }

                ChiTietVoucher ctvc = ctvcRepo.getByIdVCAndIdKh(voucher.getId(), idKh);
                ctvc.setTrangThai(2);

                ctvcRepo.save(ctvc);

                hd.setVoucher(voucher);
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
            Object soLuongObj = prod.get("soLuongTrongGio");
            Integer sL;

            if (soLuongObj instanceof Integer) {
                sL = (Integer) soLuongObj;
            } else {
                try {
                    sL = Integer.valueOf((String) prod.get("soLuongTrongGio"));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }
            }

            cthd.setSoLuong(sL);
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
        boolean checkSL = true;
        // tạo biến để get tên sản phẩm ko đạt số lượng
        String tenSPCheck = null;

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDonExisting = hoaDonOptional.get();
            hoaDonExisting.setTrangThai(3);
            hoaDonRepo.save(hoaDonExisting);

            KhachHang getKH = hoaDonExisting.getKhachHang();

            List<ChiTietHoaDon> cthdList = chiTietHoaDonRepo.findByHoaDon(hoaDonExisting.getId());

            for (ChiTietHoaDon cthd : cthdList) {
                ChiTietSanPham getCheckCTSP = chiTietSanPhamRepo.findById(cthd.getChiTietSanPham().getId()).get();
                Integer soLuong = cthd.getSoLuong();
                if (soLuong > cthdService.getTotalSoLuong(getCheckCTSP.getId())) {
                    return ResponseEntity.badRequest()
                            .body("Sản phẩm không đủ để cung cấp." + cthdService.getTotalSoLuong(getCheckCTSP.getId()));
                }

                List<LoHang> listLo = lHRepo.fByIdCTSP(getCheckCTSP.getId());
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
            }

            if (checkSL) {
                for (ChiTietHoaDon cthd : cthdList) {
                    DanhGia dg = new DanhGia();
                    ChiTietSanPham getCTSP = chiTietSanPhamRepo.findById(cthd.getChiTietSanPham().getId()).get();
                    dg.setChiTietSanPham(getCTSP);

                    getCTSP.setSoLuong(getCTSP.getSoLuong() - cthd.getSoLuong());

                    dg.setKhachHang(getKH);
                    dg.setNgayDanhGia(LocalDateTime.now());
                    dg.setTrangThai(0);

                    chiTietSanPhamRepo.save(getCTSP);
                    dgRepo.save(dg);
                }
                return ResponseEntity.ok("Xác nhận hóa đơn thành công.");
            } else {
                return ResponseEntity.badRequest()
                        .body("Sản phẩm '" + tenSPCheck + "' trong hóa đơn quá lượng trong kho.");
            }

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
            hoaDon.setMaHD(hoaDon.getMaHD());


            if(req.getMaVoucher() != null){
                Voucher vc = vcRepo.findById(req.getMaVoucher()).get();
                hoaDon.setVoucher(vc);
            }else {
                hoaDon.setVoucher(null);
            }
            hoaDon.setNgayThanhToan(LocalDateTime.now());

            // hoaDon.setNgayNhanHang(req.getNgayNhanHang());
            hoaDon.setTrangThai(3);
            hoaDon.setLoaiHD(req.getLoaiHD());
            hoaDon.setThanhtoan(0);
            hoaDon.setDiaChiNguoiNhan(req.getDiaChiNguoiNhan());
            hoaDon.setNgaySua(LocalDateTime.now());
            // hoaDon.setGhiChu(req.getGhiChu());
            System.out.println("Mã Hóa đơn : " + hoaDon.getMaHD());
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
    public ResponseEntity<?> updateGhiChu(@PathVariable String id, @RequestBody Map<String, String> payload) {
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
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                        .body("Cập nhật ghi chú hóa đơn thành công.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteHoaDon(@RequestBody Map<String, String> request) {
        String id = request.get("id");

        // Kiểm tra nếu hóa đơn không tồn tại
        if (!hoaDonRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Lấy danh sách chi tiết hóa đơn theo id hóa đơn
        List<ChiTietHoaDon> listCTHD = chiTietHoaDonRepo.getByIdHD(id);

        if (listCTHD != null && !listCTHD.isEmpty()) {
            for (ChiTietHoaDon cthd : listCTHD) {
                if (chiTietHoaDonRepo.existsById(cthd.getId())) {
                    Sort sort = Sort.by(Sort.Order.desc("loHang.hsd"));
                    List<LoHangWithHoaDon> loHangs = lhhdRepo.getByIdCTHD(cthd.getId(), sort);

                    if (loHangs != null) {
                        for (LoHangWithHoaDon lhwhh : loHangs) {
                            LoHang lh = lhwhh.getLoHang();
                            lh.setSoLuong(lh.getSoLuong() + lhwhh.getSoLuong());
                            lHRepo.save(lh);
                            lhhdRepo.deleteById(lhwhh.getId());
                        }
                    }

                    // Xóa chi tiết hóa đơn hiện tại
                    chiTietHoaDonRepo.deleteById(cthd.getId());
                }
            }
        }

        // Xóa hóa đơn chính
        hoaDonRepo.deleteById(id);

        // Trả về mã 200 nếu thành công
        return ResponseEntity.ok().build();
    }

    public byte[] createInvoicePDF(String idHD, List<ChiTietHoaDon> chiTietHoaDonList, double discountAmount,
            String customerName, double amountPaid, double totalAmount) {
        com.itextpdf.text.Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Đường dẫn font chữ
            BaseFont baseFont = BaseFont.createFont(
                    "D:\\DATN\\DATN_2024-master (1)\\DATN_2024-master\\src\\main\\resources\\arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            HoaDon hoaDon = hoaDonRepo.getReferenceById(idHD);
            // Tiêu đề hóa đơn
            Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(40); // Chiếm 40% chiều rộng
            info.setHorizontalAlignment(Element.ALIGN_LEFT); // Canh phải
            info.setSpacingBefore(20);

            info.addCell(createCellWithoutBorder("Mã hóa đơn", boldFont));
            info.addCell(createCellWithoutBorder(hoaDon.getMaHD(), normalFont));
            info.addCell(createCellWithoutBorder("Tên khách hàng", boldFont));
            info.addCell(createCellWithoutBorder(customerName, normalFont));
            info.addCell(createCellWithoutBorder("Ngày thanh toán", boldFont));
            info.addCell(createCellWithoutBorder(LocalDateTime.now().format(formatter), normalFont));
            document.add(info);
            PdfPTable detailTable = new PdfPTable(7); // 6 cột
            detailTable.setWidthPercentage(100);
            detailTable.setSpacingBefore(10);
            detailTable.setWidths(new float[] { 1, 2, 3, 2, 2, 2, 3 }); // 7 giá trị

            // Header của bảng chi tiết
            detailTable.addCell(createCellWithBorder("STT", boldFont));
            detailTable.addCell(createCellWithBorder("Mã sản phẩm", boldFont));
            detailTable.addCell(createCellWithBorder("Tên sản phẩm", boldFont));
            detailTable.addCell(createCellWithBorder("Số lượng", boldFont));
            detailTable.addCell(createCellWithBorder("Đơn giá", boldFont));
            detailTable.addCell(createCellWithBorder("Giảm giá", boldFont));
            detailTable.addCell(createCellWithBorder("Thành tiền", boldFont));

            // Thêm dữ liệu sản phẩm vào bảng
            int index = 1;
            double tongTien = 0.0;
            for (ChiTietHoaDon chiTiet : chiTietHoaDonList) {

                detailTable.addCell(createCellWithBorder(String.valueOf(index++), normalFont));
                detailTable
                        .addCell(createCellWithBorder(chiTiet.getChiTietSanPham().getSanPham().getMaSP(), normalFont));
                detailTable
                        .addCell(createCellWithBorder(chiTiet.getChiTietSanPham().getSanPham().getTenSP(), normalFont));
                detailTable.addCell(createCellWithBorder(String.valueOf(chiTiet.getSoLuong()), normalFont));
                detailTable.addCell(createCellWithBorder(chiTiet.getGiaSauGiam(), normalFont));
                detailTable
                        .addCell(createCellWithBorder(String.valueOf(chiTiet.toResponse().getTienGiam()), normalFont));
                detailTable.addCell(createCellWithBorder(
                        formatCurrency(Double.valueOf(Double.valueOf(chiTiet.getGiaSauGiam()) * chiTiet.getSoLuong())),
                        normalFont));
                tongTien += Double.valueOf(Double.valueOf(chiTiet.getGiaSauGiam()) * chiTiet.getSoLuong());
            }
            document.add(detailTable);
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(40); // Chiếm 40% chiều rộng
            infoTable.setHorizontalAlignment(Element.ALIGN_RIGHT); // Canh phải
            infoTable.setSpacingBefore(20);

            infoTable.addCell(createCellWithoutBorder("Tổng tiền:", boldFont));
            infoTable.addCell(createCellWithoutBorder(formatCurrency(tongTien), normalFont));
            infoTable.addCell(createCellWithoutBorder("Tiền giảm từ voucher:", boldFont));
            infoTable.addCell(createCellWithoutBorder(formatCurrency(tongTien - totalAmount), normalFont));
            infoTable.addCell(createCellWithoutBorder("Tiền cần thanh toán:", boldFont));
            infoTable.addCell(createCellWithoutBorder(formatCurrency(totalAmount), normalFont));
            infoTable.addCell(createCellWithoutBorder("Tiền khách đưa:", boldFont));
            infoTable.addCell(createCellWithoutBorder(formatCurrency(amountPaid), normalFont));
            infoTable.addCell(createCellWithoutBorder("Tiền thừa:", boldFont));
            infoTable.addCell(createCellWithoutBorder(formatCurrency(amountPaid - totalAmount), normalFont));

            // Thêm bảng thông tin hóa đơn vào cuối trang
            document.add(infoTable);
            // Lời cảm ơn
            Paragraph thankYou = new Paragraph("Cảm ơn quý khách đã mua sắm tại cửa hàng!", normalFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            thankYou.setSpacingBefore(30);
            document.add(thankYou);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    // Tạo ô có viền
    private PdfPCell createCellWithBorder(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5); // Khoảng cách giữa viền và nội dung
        return cell;
    }

    private PdfPCell createCellWithoutBorder(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5); // Khoảng cách giữa viền và nội dung
        cell.setBorder(PdfPCell.NO_BORDER); // Loại bỏ viền
        return cell;
    }

    // Định dạng tiền tệ
    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    @PostMapping("/send-invoice")
    public ResponseEntity<String> sendInvoice(@RequestBody MailKH invoiceRequest) {
        try {
            HoaDon hoaDon = hoaDonRepo.getReferenceById(invoiceRequest.getIdHD());
            byte[] pdfData = createInvoicePDF(
                    invoiceRequest.getIdHD(),
                    hoaDon.getChiTietHoaDons(),
                    invoiceRequest.getDiscountAmount(),
                    invoiceRequest.getCustomerName(),
                    invoiceRequest.getAmountPaid(),
                    invoiceRequest.getTotalAmount());

            emailService.sendEmailWithAttachment(
                    invoiceRequest.getEmail(),
                    "Hóa đơn thanh toán",
                    "Xin chào " + invoiceRequest.getCustomerName() +
                            ". Cảm ơn bạn đã tin tưởng và lựa chọn chúng tôi làm nơi mua sắm. Sự hài lòng của bạn là động lực lớn nhất để chúng tôi không ngừng cải thiện.\n\n"
                            +
                            "Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi qua email hoặc số hotline. Chúng tôi luôn sẵn lòng hỗ trợ.\n\n"
                            +
                            "Trân trọng,\n" +
                            "[Thực Phẩm Chức Năng Loopy]"
                            + ",\n\nĐính kèm là hóa đơn thanh toán của bạn.",
                    pdfData,
                    "hoadon.pdf");

            return ResponseEntity.ok("Email gửi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi gửi email.");
        }
    }

}
