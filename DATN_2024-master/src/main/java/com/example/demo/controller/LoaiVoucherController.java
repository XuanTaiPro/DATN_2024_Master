package com.example.demo.controller;

import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.dto.loaivoucher.LoaiVoucherRequest;
import com.example.demo.dto.loaivoucher.LoaiVoucherResponse;
import com.example.demo.dto.thongbao.ThongBaoRequest;
import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.example.demo.dto.voucher.VoucherResponse;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.LoaiVoucher;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.LoaiVoucherRepository;
import com.example.demo.repository.ThongBaoRepository;
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
@RequestMapping("loaivoucher")
public class LoaiVoucherController {
    @Autowired
    private LoaiVoucherRepository lvcRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<LoaiVoucherResponse> list = new ArrayList<>();
        lvcRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("getId")
    public String findLoaiVCByTen(@RequestParam String ten) {
        return lvcRepo.getLoaiVoucherByTen(ten).getId();
    }

    @GetMapping("page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LoaiVoucher> loaiVoucherPage = lvcRepo.findAll(pageable);

        List<LoaiVoucherResponse> list = new ArrayList<>();
        loaiVoucherPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("loaiVouchers", list);  // Chú ý tên trường phải giống với front-end
        response.put("currentPage", loaiVoucherPage.getNumber());
        response.put("totalItems", loaiVoucherPage.getTotalElements());
        response.put("totalPages", loaiVoucherPage.getTotalPages());

        if (list.isEmpty()) {
            response.put("message", "Danh sách trống");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        if (lvcRepo.findById(id).isPresent()) {
            return ResponseEntity.ok().body(lvcRepo.findById(id).stream().map(LoaiVoucher::toResponse));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id để hiển thị");
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody LoaiVoucherRequest loaiVoucherRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            System.out.println(mess.toString());
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (loaiVoucherRequest.getId() == null || loaiVoucherRequest.getId().isEmpty()) {
            loaiVoucherRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (loaiVoucherRequest.getMa() == null || loaiVoucherRequest.getMa().isEmpty()) {//nếu mã chưa đc điền thì tự động thêm mã
            loaiVoucherRequest.setMa(generateCodeAll.generateMaLVC());
        }
        LoaiVoucher loaiVoucher = loaiVoucherRequest.toEntity();
        loaiVoucher.setNgayTao(LocalDateTime.now());
        lvcRepo.save(loaiVoucher);
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody LoaiVoucherRequest loaiVoucherRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
//        if (lvcRepo.findById(id).isPresent()) {
//            LoaiVoucher loaiVoucher = loaiVoucherRequest.toEntity();
//            loaiVoucher.setId(id);
//            lvcRepo.save(loaiVoucher);
//            return ResponseEntity.ok("Update thành công ");
//        } else {
//            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
//        }

        Optional<LoaiVoucher> optionalLoaiVoucher = lvcRepo.findById(id);
        if (optionalLoaiVoucher.isPresent()) {
            LoaiVoucher loaiVoucher = optionalLoaiVoucher.get();
            LoaiVoucher loaiVoucherUpdate = loaiVoucherRequest.toEntity();
            loaiVoucherUpdate.setId(id);
            loaiVoucherUpdate.setNgaySua(LocalDateTime.now());
            loaiVoucher.setNgayTao(optionalLoaiVoucher.get().getNgayTao());
            loaiVoucherUpdate.setMa(optionalLoaiVoucher.get().getMa());

            LoaiVoucher saveLoaiVoucher = lvcRepo.save(loaiVoucherUpdate);  // Lưu thay đổi và lấy đối tượng đã lưu
            return ResponseEntity.ok(saveLoaiVoucher);  // Trả về đối tượng đã cập nhật
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();  // Khởi tạo Map để trả về JSON hợp lệ
        if (lvcRepo.findById(id).isPresent()) {
            lvcRepo.deleteById(id);
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response);  // Trả về phản hồi JSON
        } else {
            response.put("message", "Không tìm thấy id cần xóa");
            return ResponseEntity.badRequest().body(response);  // Trả về phản hồi JSON khi lỗi
        }
    }

    @GetMapping("search")
    public ResponseEntity<?> searchLoaiVoucher(@RequestParam String ten) {
        List<LoaiVoucherResponse> list = new ArrayList<>();
        lvcRepo.findByTenContainingIgnoreCase(ten).forEach(voucher -> list.add(voucher.toResponse()));

        if (list.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy voucher với tên: " + ten);
        }
        return ResponseEntity.ok(list);
    }
}
