package com.example.demo.controller;

import com.example.demo.dto.khachhang.KhachHangRequest;
import com.example.demo.dto.khachhang.KhachHangRequestOnline;
import com.example.demo.dto.khachhang.KhachHangResponse;
import com.example.demo.entity.KhachHang;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.service.GenerateCodeAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @GetMapping
    public List<KhachHang> findAll() {
        return khRepo.findAll();
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean existsInNhanVien = nhanVienRepository.existsByEmail(email);
        boolean existsInKhachHang = khRepo.existsByEmail(email);
        boolean exists = existsInNhanVien || existsInKhachHang;
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/khachhangud/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email, @RequestParam String id) {
        boolean existsInKhachHang = khRepo.existsByEmailAndIdNot(email, id);
        boolean existsInNhanVien = nhanVienRepository.existsByMaAndIdNot(email, id);
        boolean exists = existsInNhanVien || existsInKhachHang;
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/khachhangud/check-phone")
    public boolean checkPhone(@RequestParam String sdt, @RequestParam String id) {
        return khRepo.existsBySdtAndIdNot(sdt, id);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhoneExists(@RequestParam("sdt") String sdt) {
        boolean exists = khRepo.existsBySdt(sdt);
        return ResponseEntity.ok(exists);
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
            @RequestParam(defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> khachHangPage = khRepo.findAll(pageable);

        List<KhachHangResponse> list = new ArrayList<>();
        khachHangPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("khachHangs", list); // Chú ý tên trường phải giống với front-end
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
    public ResponseEntity<?> add(@RequestBody KhachHangRequest khachHangRequest) {

        if (khachHangRequest.getMa() == null || khachHangRequest.getMa().isEmpty()) {
            khachHangRequest.setMa(generateCodeAll.generateMaKhachHang());
        }
        if (khachHangRequest.getId() == null || khachHangRequest.getId().isEmpty()) {
            khachHangRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        KhachHang khachHang = khachHangRequest.toEntity();
        khachHang.setNgayTao(LocalDateTime.now());
        khRepo.save(khachHang);
        return ResponseEntity.ok(khRepo.findAll());
    }

    @PostMapping("addKH")
    public ResponseEntity<?> addKH(@RequestBody KhachHangRequest khachHangRequest) {

        if (khachHangRequest.getMa() == null || khachHangRequest.getMa().isEmpty()) {
            khachHangRequest.setMa(generateCodeAll.generateMaKhachHang());
        }
        if (khachHangRequest.getId() == null || khachHangRequest.getId().isEmpty()) {
            khachHangRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        KhachHang khachHang = khachHangRequest.toEntity();
        khachHang.setNgayTao(LocalDateTime.now());
        khachHang.setTrangThai(1);
        khRepo.save(khachHang);
        return ResponseEntity.ok(khRepo.findAll());
    }

    @GetMapping("detailByEmail")
    public ResponseEntity<?> khByEmail(@RequestParam String email){
        return ResponseEntity.ok(khRepo.getKhachHangByEmail(email));
    }

    @PostMapping("dangKy")
    public ResponseEntity<?> dangKy(@RequestBody KhachHangRequestOnline khachHangRequest) {
        khachHangRequest.setId(Optional.ofNullable(khachHangRequest.getId())
                .filter(id -> !id.isEmpty())
                .orElse(UUID.randomUUID().toString().substring(0, 8).toUpperCase()));

        KhachHang khachHang = khachHangRequest.toEntity();
        khachHang.setNgayTao(LocalDateTime.now());
        khachHang.setMa(generateUniqueMa());
        khachHang.setPassw(khachHangRequest.getPassw());
        khachHang.setEmail(khachHangRequest.getEmail());
        khachHang.setDiaChi(khachHangRequest.getDiaChi());
        khachHang.setGioiTinh(khachHangRequest.getGioiTinh());
        khachHang.setId(khachHangRequest.getId());
        khachHang.setTrangThai(1);
        khachHang.setSdt(khachHangRequest.getSdt());

        khRepo.save(khachHang);
        return ResponseEntity.ok("Thêm thành công");
    }

    // Hàm tạo mã khách hàng đăng ký
    private String generateUniqueMa() {
        String generatedMa;
        do {
            String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            generatedMa = "KH" + randomString;
        } while (khRepo.getByMa(generatedMa) != null);
        return generatedMa;
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody KhachHangRequest khachHangRequest,
            BindingResult bindingResult) {
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
    public ResponseEntity<?> updateOnline(@PathVariable String id,
            @Valid @RequestBody KhachHangRequestOnline kHRequestOnline, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            result.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        KhachHang kh = khRepo.findById(id).orElse(null);
        if (kh != null) {
            KhachHang khachHang = kHRequestOnline.toEntity();

            khachHang.setId(id);
            khachHang.setNgaySua(LocalDateTime.now());
            khachHang.setNgayTao(kh.getNgayTao());
            khachHang.setMa(kh.getMa());
            khachHang.setPassw(kh.getPassw());
            khachHang.setTrangThai(kh.getTrangThai());

            khRepo.save(khachHang);
            return ResponseEntity.ok("Update thành công");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    // @DeleteMapping("delete/{id}")
    // public ResponseEntity<?> delete(@PathVariable String id) {
    // Map<String, String> response = new HashMap<>(); // Khởi tạo Map để trả về
    // JSON hợp lệ
    // if (khRepo.findById(id).isPresent()) {
    // khRepo.deleteById(id);
    // response.put("message", "Xóa thành công");
    // return ResponseEntity.ok(response); // Trả về phản hồi JSON
    // } else {
    // response.put("message", "Không tìm thấy id cần xóa");
    // return ResponseEntity.badRequest().body(response); // Trả về phản hồi JSON
    // khi lỗi
    // }
    // }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        // if (LoginController.tenQuyen == null ||
        // !LoginController.tenQuyen.equalsIgnoreCase("Admin")) {
        // return ResponseEntity.status(403).body(Map.of("success", false, "message",
        // "Chỉ có Admin mới có quyền xóa!"));
        // }
        KhachHang khachHang = khRepo.getReferenceById(id);
        khachHang.setTrangThai(0);
        khRepo.save(khachHang);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Cập nhật trạng thái thành công!");
    }

    @DeleteMapping("deleteback/{id}")
    public ResponseEntity<?> deleteback(@PathVariable String id) {
        // if (LoginController.tenQuyen == null ||
        // !LoginController.tenQuyen.equalsIgnoreCase("Admin")) {
        // return ResponseEntity.status(403).body(Map.of("success", false, "message",
        // "Chỉ có Admin mới có quyền xóa!"));
        // }
        KhachHang khachHang = khRepo.getReferenceById(id);
        khachHang.setTrangThai(1);
        khRepo.save(khachHang);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Cập nhật trạng thái thành công!");
    }

    @GetMapping("search-filter")
    public ResponseEntity<?> searchAndFilterKhachHang(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) String gioiTinh,
            @RequestParam(required = false) String diaChi,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> khachHangs = khRepo.timKiemVaLocKhachHang(ten, gioiTinh, diaChi, trangThai, pageable);

        if (khachHangs.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Không tìm thấy khách hàng với tiêu chí tìm kiếm.",
                    "khachHangs", Collections.emptyList(),
                    "currentPage", page,
                    "totalPages", 0));
        }
        List<KhachHangResponse> khachHangResponses = khachHangs.stream().map(KhachHang::toResponse).toList();

        return ResponseEntity.ok(Map.of(
                "khachHangs", khachHangResponses,
                "currentPage", khachHangs.getNumber(),
                "totalPages", khachHangs.getTotalPages()));
    }

    @GetMapping("search")
    public ResponseEntity<?> tkVaLocKH(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) String gioiTinh,
            @RequestParam(required = false) String sdt) {
        List<KhachHang> khachHangList = khRepo.tkVaLocKhachHang(ten, gioiTinh, sdt);
        List<KhachHangResponse> khachHangResponses = khachHangList.stream().map(KhachHang::toResponse).toList();
        return ResponseEntity.ok(khachHangResponses);
    }
}
