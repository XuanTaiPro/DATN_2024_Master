package com.example.demo.controller;

import com.example.demo.dto.nhanvien.NhanVienRequest;
import com.example.demo.dto.nhanvien.NhanVienResponse;
import com.example.demo.dto.nhanvien.ResponseMessage;
import com.example.demo.entity.NhanVien;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.QuyenRepository;
import com.example.demo.service.GenerateCodeAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("nhanvien")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nvRepo;
    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private QuyenRepository qRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<NhanVienResponse> list = new ArrayList<>();
        nvRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean existsInNhanVien = nvRepo.existsByEmail(email);
        boolean existsInKhachHang = khachHangRepository.existsByEmail(email);
        boolean exists = existsInNhanVien || existsInKhachHang;
        return ResponseEntity.ok(exists);
    }
    @GetMapping("/nhavienud/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email, @RequestParam String id) {
        boolean existsInKhachHang= khachHangRepository.existsByEmailAndIdNot(email, id);
        boolean existsInNhanVien= nvRepo.existsByMaAndIdNot(email, id);
        boolean exists = existsInNhanVien || existsInKhachHang;
        return ResponseEntity.ok(exists);
    }
    @GetMapping("page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVien> nhanVienPage = nvRepo.findAll(pageable);

        List<NhanVienResponse> list = new ArrayList<>();
        nhanVienPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("nhanViens", list);
        response.put("currentPage", nhanVienPage.getNumber());
        response.put("totalItems", nhanVienPage.getTotalElements());
        response.put("totalPages", nhanVienPage.getTotalPages());

        if (list.isEmpty()) {
            response.put("message", "Danh sách nhân viên trống");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        if (nvRepo.findById(id).isPresent()) {
            return ResponseEntity.ok().body(nvRepo.findById(id).stream().map(NhanVien::toResponse));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id để hiển thị");
        }
    }

    @GetMapping("profile/{id}")
    public ResponseEntity<?> profile(@PathVariable String id) {
        NhanVien nhanVien = nvRepo.findById(id).orElse(null);
        if (nhanVien == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Nhân viên không tồn tại"));
        }
        return ResponseEntity.ok(nhanVien.toResponse());
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody NhanVienRequest nhanVienRequest, BindingResult bindingResult) {
//        if (LoginController.tenQuyen == null ||
//                !LoginController.tenQuyen.equalsIgnoreCase("Admin")) {
//            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Chỉ Admin mới có quyền Thêm!"));
//        }
        if (nvRepo.existsByEmail(nhanVienRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email đã tồn tại"));
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        if (nhanVienRequest.getMa() == null || nhanVienRequest.getMa().isEmpty()) {
            nhanVienRequest.setMa(generateCodeAll.generateMaNhanVien());
        }

        if (nhanVienRequest.getId() == null || nhanVienRequest.getId().isEmpty()) {
            nhanVienRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        NhanVien nhanVien = nhanVienRequest.toEntity();
        nhanVien.setQuyen(qRepo.getById(nhanVienRequest.getIdQuyen()));
        nhanVien.setNgayTao(LocalDateTime.now());
        nvRepo.save(nhanVien);

        try {
            nvRepo.save(nhanVien); // Lưu nhân viên vào cơ sở dữ liệu
        } catch (Exception e) {
            e.printStackTrace();  // In lỗi chi tiết vào console để dễ dàng kiểm tra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Lỗi khi thêm nhân viên: " + e.getMessage()));
        }

        return ResponseEntity.ok(new ResponseMessage("Thêm nhân viên thành công"));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody NhanVienRequest nhanVienRequest,
                                    BindingResult bindingResult) {

//        if (LoginController.tenQuyen == null ||
//                !LoginController.tenQuyen.equalsIgnoreCase("Admin")) {
//            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Chỉ Admin mới có quyền cập nhật!"));
//        }

        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        Optional<NhanVien> optionalNhanVien = nvRepo.findById(id);

        if (optionalNhanVien.isPresent()) {
            NhanVien nhanVien = optionalNhanVien.get();
            if (nhanVienRequest.getImg() == null || nhanVienRequest.getImg().isEmpty()) {
                nhanVienRequest.setImg(nhanVien.getImg());
            }
            NhanVien nhanVienUpdate = nhanVienRequest.toEntity();
            nhanVienUpdate.setId(id);
            nhanVienUpdate.setQuyen(qRepo.getById(nhanVienRequest.getIdQuyen()));
            nhanVienUpdate.setMa(optionalNhanVien.get().getMa());
            nhanVienUpdate.setNgaySua(LocalDateTime.now());
            nhanVienUpdate.setNgayTao(optionalNhanVien.get().getNgayTao());
//            NhanVien savedNhanVien = nvRepo.save(nhanVienUpdate);
//
//            return ResponseEntity.ok(savedNhanVien);
            try {
                nvRepo.save(nhanVienUpdate);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Lỗi khi update nhân viên: " + e.getMessage()));
            }

            return ResponseEntity.ok(new ResponseMessage("update nhân viên thành công"));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
//        if (LoginController.tenQuyen == null ||
//                !LoginController.tenQuyen.equalsIgnoreCase("Admin")) {
//            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Chỉ có Admin mới có quyền xóa!"));
//        }
        NhanVien nhanVien = nvRepo.getReferenceById(id);
        nhanVien.setTrangThai(0);
        nvRepo.save(nhanVien);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Cập nhật trạng thái nhân viên thành công!");
    }

    @DeleteMapping("deleteback/{id}")
    public ResponseEntity<?> deleteback(@PathVariable String id) {
//        if (LoginController.tenQuyen == null ||
//                !LoginController.tenQuyen.equalsIgnoreCase("Admin")) {
//            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Chỉ có Admin mới có quyền xóa!"));
//        }
        NhanVien nhanVien = nvRepo.getReferenceById(id);
        nhanVien.setTrangThai(1);
        nvRepo.save(nhanVien);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Cập nhật trạng thái nhân viên thành công!");
    }

    @GetMapping("search-filter")
    public ResponseEntity<?> searchAndFilterNhanVienWithPagination(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) String gioiTinh,
            @RequestParam(required = false) String diaChi,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVien> nhanViens = nvRepo.timKiemVaLocNhanVien(ten, gioiTinh, diaChi, trangThai, pageable);

        if (nhanViens.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Không tìm thấy nhân viên với tiêu chí tìm kiếm.",
                    "nhanViens", Collections.emptyList(),
                    "currentPage", page,
                    "totalPages", 0
            ));
        }
        List<NhanVienResponse> nhanVienResponses = nhanViens.stream().map(NhanVien::toResponse).toList();

        return ResponseEntity.ok(Map.of(
                "nhanViens", nhanVienResponses,
                "currentPage", nhanViens.getNumber(),
                "totalPages", nhanViens.getTotalPages()
        ));
    }


}
