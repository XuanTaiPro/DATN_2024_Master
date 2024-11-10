package com.example.demo.controller;

import com.example.demo.dto.khachhang.KhachHangRequest;
import com.example.demo.dto.khachhang.KhachHangRequestOnline;
import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.dto.voucher.VoucherResponse;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.KhachHangRepository;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping
    public List<KhachHang> findAll() {
        return khRepo.findAll();
    }

    @GetMapping("getId")
    public String findKhachHangByTen(@RequestParam String ten) {
        return khRepo.getKhachHangByTen(ten).getId();
    }

    @GetMapping("findAllNotPW")
    public ResponseEntity<?> findAllNotPW() {
        List<KhachHangResponse> list = new ArrayList<>();
        khRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> khachHangPage = khRepo.findAll(pageable);

        List<KhachHangResponse> list = new ArrayList<>();
        khachHangPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("khachHangs", list);  // Chú ý tên trường phải giống với front-end
        response.put("currentPage", khachHangPage.getNumber());
        response.put("totalItems", khachHangPage.getTotalElements());
        response.put("totalPages", khachHangPage.getTotalPages());

        if (list.isEmpty()) {
            response.put("message", "Danh sách khách hàng trống");
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping("pageNotPW")
    public ResponseEntity<?> pageNotPW(@RequestParam(defaultValue = "0") Integer page) {
        Pageable p = PageRequest.of(page, 10);
        List<KhachHangResponse> list = new ArrayList<>();
        khRepo.findAll(p).forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }


    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        if (khRepo.findById(id).isPresent()) {
            return ResponseEntity.ok().body(khRepo.findById(id).get().toResponse());
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id để hiển thị");
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody KhachHangRequest khachHangRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            System.out.println(mess.toString());
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (khachHangRequest.getMa() == null || khachHangRequest.getMa().isEmpty()) {
            khachHangRequest.setMa(generateCodeAll.generateMaKhachHang());
        }
        if (khachHangRequest.getId() == null || khachHangRequest.getId().isEmpty()) {
            khachHangRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        KhachHang khachHang = khachHangRequest.toEntity();
        khachHang.setNgayTao(LocalDateTime.now());
        khRepo.save(khachHang);
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody KhachHangRequest khachHangRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            System.out.println(mess.toString());
            return ResponseEntity.badRequest().body(mess.toString());
        }
        Optional<KhachHang> optionalKhachHang = khRepo.findById(id);
        if (optionalKhachHang.isPresent()) {
            KhachHang khachHang = optionalKhachHang.get();
            KhachHang khachHangUpdate = khachHangRequest.toEntity();
            khachHangUpdate.setId(id);
            khachHangUpdate.setNgaySua(LocalDateTime.now());
            khachHang.setNgayTao(optionalKhachHang.get().getNgayTao());
            KhachHang saveKH = khRepo.save(khachHangUpdate);
            return ResponseEntity.ok(saveKH);
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @PutMapping("updateOnline/{id}")
    public ResponseEntity<?> updateOnline(@PathVariable String id, @Valid @RequestBody KhachHangRequestOnline khachHangRequestOnlinest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            System.out.println(mess.toString());
            return ResponseEntity.badRequest().body(mess.toString());
        }
        Optional<KhachHang> optionalKhachHang = khRepo.findById(id);
        if (optionalKhachHang.isPresent()) {

            KhachHang khachHang = optionalKhachHang.get();
            KhachHang khachHangUpdateOnline = khachHangRequestOnlinest.toEntity();

            khachHangUpdateOnline.setId(id);
            khachHangUpdateOnline.setNgaySua(LocalDateTime.now());
            khachHang.setNgayTao(optionalKhachHang.get().getNgayTao());
            khachHang.setMa(optionalKhachHang.get().getMa());
            khachHang.setPassw(optionalKhachHang.get().getPassw());
            khachHang.setTrangThai(optionalKhachHang.get().getTrangThai());

            KhachHang saveKH = khRepo.save(khachHangUpdateOnline);
            return ResponseEntity.ok(saveKH);
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteKhachHang(@PathVariable String id) {
        Optional<KhachHang> khachHang = khRepo.findById(id);
        if (khachHang.isPresent()) {
            khRepo.delete(khachHang.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("search")
    public ResponseEntity<?> searchVoucher(@RequestParam String ten) {
        List<KhachHangResponse> list = new ArrayList<>();
        khRepo.findByTenContainingIgnoreCase(ten).forEach(voucher -> list.add(voucher.toResponse()));
        if (list.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy voucher với tên: " + ten);
        }
        return ResponseEntity.ok(list);
    }
}
