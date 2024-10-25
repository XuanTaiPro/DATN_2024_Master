package com.example.demo.controller;

import com.example.demo.entity.DanhMuc;
import com.example.demo.repository.DanhMucRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("danh-muc")
public class DanhMucController {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<DanhMuc> danhMucList = danhMucRepository.findAll(sort);
        return ResponseEntity.ok(danhMucList);
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return ResponseEntity.ok(danhMucRepository.findAll(pageRequest));
    }
    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        DanhMuc danhMuc = danhMucRepository.getById(id);
        if (danhMuc == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + id);
        }
        return ResponseEntity.ok(danhMuc);
    }
    @PostMapping("/detailByMa")
    public ResponseEntity<?> detailByMa(@RequestBody Map<String, String> request) {
        String ma = request.get("ma");
        if (ma == null || ma.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã không được để trống.");
        }
        if (!Pattern.matches("^DM[A-Z0-9]{8}$", ma.trim())) {
            return ResponseEntity.badRequest().body("Mã phải có định dạng DMXXXXXXXX (X là chữ cái hoặc số)!");
        }
        DanhMuc danhMuc = danhMucRepository.getByMa(ma);
        if (danhMuc == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có mã: " + ma);
        }
        return ResponseEntity.ok(danhMuc);
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody DanhMuc danhMuc) {
        danhMuc.setNgayTao(LocalDateTime.now());
        danhMuc.setNgaySua(null);

        // Kiểm tra tên danh mục
        if (danhMucRepository.getByName(danhMuc.getTen().trim()) != null) {
            return ResponseEntity.badRequest().body("Tên danh mục không được trùng!");
        }

        String prefix = "DM";
        String uniqueID;
        do {
            uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (danhMucRepository.getByMa(prefix + uniqueID) != null);
        danhMuc.setMa(prefix + uniqueID);

        danhMucRepository.save(danhMuc);
        return ResponseEntity.ok("Thêm danh mục thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DanhMuc danhMuc) {
        String id = danhMuc.getId();
        if (id == null || id.isEmpty() || danhMucRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + id);
        }
        if (danhMucRepository.getByNameAndId(id, danhMuc.getTen().trim()) != null) {
            return ResponseEntity.badRequest().body("Tên danh mục không được trùng!");
        }
        DanhMuc danhMucUpdate = danhMucRepository.getReferenceById(id);
        danhMucUpdate.setNgaySua(LocalDateTime.now());
        BeanUtils.copyProperties(danhMuc, danhMucUpdate, "id", "ngayTao", "ma"); // Giữ nguyên ngày tạo, mã
        danhMucRepository.save(danhMucUpdate);
        return ResponseEntity.ok("Cập nhật danh mục thành công!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.isEmpty() || danhMucRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + id);
        }
        danhMucRepository.deleteById(id);
        return ResponseEntity.ok("Xóa danh mục thành công!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
