package com.example.demo.controller;

import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.dto.thongbao.ThongBaoRequest;
import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.example.demo.dto.voucher.VoucherRequest;
import com.example.demo.dto.voucher.VoucherResponse;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.ThongBao;
import com.example.demo.entity.Voucher;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.LoaiVoucherRepository;
import com.example.demo.repository.VoucherRepository;
import com.example.demo.service.GenerateCodeAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("voucher")
public class VoucherController {
    @Autowired
    private VoucherRepository vcRepo;

    @Autowired
    private LoaiVoucherRepository lvcRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<VoucherResponse> list = new ArrayList<>();
        vcRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
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
        if (voucherRequest.getMa() == null || voucherRequest.getMa().isEmpty()) {//nếu mã chưa đc điền thì tự động thêm mã
            voucherRequest.setMa(generateCodeAll.generateMaVoucher());
        }
//        if (vcRepo.existsByMa(voucherRequest.getMa())) {
//            return ResponseEntity.badRequest().body("mã đã tồn tại");
//        }
        Voucher voucher = voucherRequest.toEntity();
        voucher.setLoaiVoucher(lvcRepo.getById(voucherRequest.getIdLoaiVC()));
        voucher.setNgayTao(LocalDateTime.now());
        vcRepo.save(voucher);
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody VoucherRequest voucherRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
//        if (vcRepo.findById(id).isPresent()) {
//            Voucher voucher = voucherRequest.toEntity();
//            voucher.setId(id);
//            voucher.setLoaiVoucher(lvcRepo.getById(voucherRequest.getIdLoaiVC()));
//            vcRepo.save(voucher);
//            return ResponseEntity.ok("Update thành công ");
//        } else {
//            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
//        }
        Optional<Voucher> optionalVoucher = vcRepo.findById(id);
        if (optionalVoucher.isPresent()) {

            Voucher voucherUpdate = voucherRequest.toEntity();
            voucherRequest.setId(id);
            voucherUpdate.setLoaiVoucher(lvcRepo.getById(voucherRequest.getIdLoaiVC()));
            voucherUpdate.setMa(optionalVoucher.get().getMa());
            voucherUpdate.setNgaySua(LocalDateTime.now());
            voucherUpdate.setNgayTao(optionalVoucher.get().getNgayTao());
            Voucher savedVoucher = vcRepo.save(voucherUpdate);  // Lưu thay đổi và lấy đối tượng đã lưu
            return ResponseEntity.ok(savedVoucher);  // Trả về đối tượng đã cập nhật
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();  // Khởi tạo Map để trả về JSON hợp lệ
        if (vcRepo.findById(id).isPresent()) {
            vcRepo.deleteById(id);
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response);  // Trả về phản hồi JSON
        } else {
            response.put("message", "Không tìm thấy id cần xóa");
            return ResponseEntity.badRequest().body(response);  // Trả về phản hồi JSON khi lỗi
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
