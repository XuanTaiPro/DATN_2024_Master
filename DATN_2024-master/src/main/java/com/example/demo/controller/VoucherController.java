package com.example.demo.controller;

import com.example.demo.dto.voucher.VoucherRequest;
import com.example.demo.dto.voucher.VoucherResponse;
import com.example.demo.dto.voucher.VoucherThanhToan;
import com.example.demo.entity.*;
import com.example.demo.repository.ChiTietVoucherRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.LoaiVoucherRepository;
import com.example.demo.repository.VoucherRepository;
import com.example.demo.service.GenerateCodeAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("voucher")
public class VoucherController {
    @Autowired
    private VoucherRepository vcRepo;

    @Autowired
    private LoaiVoucherRepository lvcRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private ChiTietVoucherRepository ctvcRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<VoucherResponse> list = new ArrayList<>();
        vcRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/VCkhachHang/{idKH}")
    public ResponseEntity<Map<String, Object>> getVoucherByKhachHang(
            @PathVariable String idKH,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ChiTietVoucher> chiTietVoucherPage = ctvcRepo.findByKhachHang_Id(idKH, pageable);

        List<VoucherThanhToan> vouchers = chiTietVoucherPage.getContent().stream()
                .filter(ctv -> ctv.getVoucher() != null)
                .map(ctv -> new VoucherThanhToan(
                        ctv.getVoucher().getId(),
                        ctv.getVoucher().getTen(),
                        ctv.getVoucher().getGiamGia(),
                        ctv.getVoucher().getGiamMin(),
                        ctv.getVoucher().getGiamMax(),
                        ctv.getVoucher().getNgayKetThuc(),
                        ctv.getVoucher().getSoLuong(),
                        ctv.getVoucher().getTrangThai()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("vouchers", vouchers);
        response.put("currentPageVC", chiTietVoucherPage.getNumber());
        response.put("totalPagesVC", chiTietVoucherPage.getTotalPages());
        response.put("totalItemsVC", chiTietVoucherPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyVoucher(@RequestBody Map<String, String> data) {
        String voucherId = data.get("id");
        if (voucherId == null || voucherId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID của voucher không được bỏ trống.");
        }
        Voucher voucher = vcRepo.findById(voucherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voucher không tồn tại"));

        if (voucher.getSoLuong() > 0) {
            voucher.setSoLuong(voucher.getSoLuong() - 1);
            if (voucher.getSoLuong() == 0) {
                voucher.setTrangThai(0);
            } else {
                voucher.setTrangThai(1);
            }
            vcRepo.save(voucher);
            return ResponseEntity.ok("Voucher đã được áp dụng thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Voucher này đã hết số lượng.");
        }
    }

    @GetMapping("/{id}/customers")
    public ResponseEntity<List<KhachHang>> getCustomersByVoucherId(@PathVariable("id") String voucherId) {
        // Lấy danh sách IDKH từ CHITIETVOUCHER theo IDVC
        List<String> idKH = ctvcRepo.findCustomerIdsByVoucherId(voucherId);
        // Tìm chi tiết khách hàng theo danh sách IDKH
        List<KhachHang> khachHangList = khRepo.findAllById(idKH);

        return ResponseEntity.ok(khachHangList);
    }

    @GetMapping("page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Voucher> voucherPage = vcRepo.findAll(pageable);

        List<VoucherResponse> list = new ArrayList<>();
        voucherPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("vouchers", list);
        response.put("currentPage", voucherPage.getNumber());
        response.put("totalItems", voucherPage.getTotalElements());
        response.put("totalPages", voucherPage.getTotalPages());

        if (list.isEmpty()) {
            response.put("message", "Danh sách trống");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return ResponseEntity.ok().body(vcRepo.findById(id).stream().map(Voucher::toResponse));
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody VoucherRequest voucherRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            System.out.println(mess.toString());
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (voucherRequest.getId() == null || voucherRequest.getId().isEmpty()) {
            voucherRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (voucherRequest.getMa() == null || voucherRequest.getMa().isEmpty()) {// nếu mã chưa đc điền thì tự động thêm
                                                                                 // mã
            voucherRequest.setMa(generateCodeAll.generateMaVoucher());
        }
        Voucher voucher = voucherRequest.toEntity();
        voucher.setLoaiVoucher(lvcRepo.getById(voucherRequest.getIdLoaiVC()));
        voucher.setNgayTao(LocalDateTime.now());
        vcRepo.save(voucher);

        if (voucherRequest.getIdKH() != null && !voucherRequest.getIdKH().isEmpty()) {
            for (String customerId : voucherRequest.getIdKH()) {
                Optional<KhachHang> khachHang = khRepo.findById(customerId);
                if (khachHang.isPresent()) {
                    ChiTietVoucher chiTietVoucher = new ChiTietVoucher();
                    chiTietVoucher.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    chiTietVoucher.setKhachHang(khachHang.get());
                    chiTietVoucher.setVoucher(voucher);
                    ctvcRepo.save(chiTietVoucher); // Lưu chi tiết voucher cho từng khách hàng
                } else {
                    return ResponseEntity.badRequest().body("Khách hàng với ID " + customerId + " không tồn tại");
                }
            }
        }
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody VoucherRequest voucherRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        Optional<Voucher> optionalVoucher = vcRepo.findById(id);
        if (optionalVoucher.isPresent()) {

            Voucher voucherUpdate = voucherRequest.toEntity();
            voucherUpdate.setId(id);
            voucherUpdate.setLoaiVoucher(lvcRepo.getById(voucherRequest.getIdLoaiVC()));
            voucherUpdate.setMa(optionalVoucher.get().getMa());
            voucherUpdate.setNgaySua(LocalDateTime.now());
            voucherUpdate.setNgayTao(optionalVoucher.get().getNgayTao());
            if (voucherUpdate.getSoLuong() > 0) {
                voucherUpdate.setTrangThai(1); // Hoạt động
            } else {
                voucherUpdate.setTrangThai(0); // Không hoạt động
            }
            Voucher savedVoucher = vcRepo.save(voucherUpdate);

            if (!ctvcRepo.getCTVC(id).isEmpty()) {
                ctvcRepo.deleteByVoucherId(id);
            }
            if (voucherRequest.getIdKH() != null && !voucherRequest.getIdKH().isEmpty()) {
                for (String customerId : voucherRequest.getIdKH()) {
                    Optional<KhachHang> khachHang = khRepo.findById(customerId);
                    if (khachHang.isPresent()) {
                        ChiTietVoucher chiTietVoucher = new ChiTietVoucher();
                        chiTietVoucher.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                        chiTietVoucher.setKhachHang(khachHang.get());
                        chiTietVoucher.setVoucher(voucherUpdate);
                        ctvcRepo.save(chiTietVoucher);
                    } else {
                        return ResponseEntity.badRequest().body("Khách hàng với ID " + customerId + " không tồn tại");
                    }
                    System.out.println(customerId);
                }
            }

            return ResponseEntity.ok(savedVoucher);
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("deleteByidVC/{idVC}")
    public ResponseEntity<?> deleteByIdVC(@PathVariable String idVC) {
        ctvcRepo.deleteByVoucherId(idVC);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, String> response = new HashMap<>(); // Khởi tạo Map để trả về JSON hợp lệ
        if (vcRepo.findById(id).isPresent()) {
            vcRepo.deleteById(id);
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response); // Trả về phản hồi JSON
        } else {
            response.put("message", "Không tìm thấy id cần xóa");
            return ResponseEntity.badRequest().body(response); // Trả về phản hồi JSON khi lỗi
        }
    }

    @GetMapping("search")
    public ResponseEntity<?> searchVoucher(@RequestParam String ten) {
        List<VoucherResponse> list = new ArrayList<>();
        vcRepo.findByTenContainingIgnoreCase(ten).forEach(voucher -> list.add(voucher.toResponse()));

        if (list.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy voucher với tên: " + ten);
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("filter")
    public ResponseEntity<?> filterVoucher(
            @RequestParam(required = false) String giamMin,
            @RequestParam(required = false) String giamMax,
            @RequestParam(required = false) String ngayKetThuc) {

        List<VoucherResponse> list = new ArrayList<>();
        vcRepo.filterVouchers(giamMin, giamMax, ngayKetThuc).forEach(voucher -> list.add(voucher.toResponse()));

        if (list.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy voucher nào phù hợp.");
        }

        return ResponseEntity.ok(list);
    }
}
