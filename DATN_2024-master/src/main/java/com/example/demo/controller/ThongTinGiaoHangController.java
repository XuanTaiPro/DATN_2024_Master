package com.example.demo.controller;

import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangRequest;
import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangResponse;
import com.example.demo.entity.ThongBao;
import com.example.demo.entity.ThongTinGiaoHang;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.ThongTinGiaoHangRepository;
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
@RequestMapping("thongtingiaohang")
public class ThongTinGiaoHangController {
    @Autowired
    private ThongTinGiaoHangRepository ttghRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<ThongTinGiaoHangResponse> list = new ArrayList<>();
        ttghRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ThongTinGiaoHang> thongTinGiaoHangPage = ttghRepo.findAll(pageable);

        List<ThongTinGiaoHangResponse> list = new ArrayList<>();
        thongTinGiaoHangPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("thongTinGiaoHangs", list);
        response.put("currentPage", thongTinGiaoHangPage.getNumber());
        response.put("totalItems", thongTinGiaoHangPage.getTotalElements());
        response.put("totalPages", thongTinGiaoHangPage.getTotalPages());

        if (list.isEmpty()) {
            response.put("message", "Danh sách trống");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return ResponseEntity.ok().body(ttghRepo.findById(id).stream().map(ThongTinGiaoHang::toResponse));
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody ThongTinGiaoHangRequest thongTinGiaoHangRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (thongTinGiaoHangRequest.getId() == null || thongTinGiaoHangRequest.getId().isEmpty()) {
            thongTinGiaoHangRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        //khách hàng này đã có địa chỉ này rồi k thêm được
        if (ttghRepo.existsByKhachHang_IdAndDcNguoiNhan(thongTinGiaoHangRequest.getIdKH(), thongTinGiaoHangRequest.getDcNguoiNhan())) {
            return ResponseEntity.badRequest().body("Địa chỉ nhận hàng đã tồn tại cho khách hàng này.");
        }
        ThongTinGiaoHang thongTinGiaoHang = thongTinGiaoHangRequest.toEntity();
        thongTinGiaoHang.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
        thongTinGiaoHang.setNgayTao(LocalDateTime.now());
        ttghRepo.save(thongTinGiaoHang);
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody ThongTinGiaoHangRequest thongTinGiaoHangRequest,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
//        if (ttghRepo.existsByKhachHang_IdAndDcNguoiNhan(thongTinGiaoHangRequest.getIdKH(), thongTinGiaoHangRequest.getDcNguoiNhan())) {
//            return ResponseEntity.badRequest().body("Địa chỉ nhận hàng đã tồn tại cho khách hàng này.");
//        }
//        if (ttghRepo.findById(id).isPresent()) {
//            ThongTinGiaoHang thongTinGiaoHang = thongTinGiaoHangRequest.toEntity();
//            thongTinGiaoHang.setId(id);
//            thongTinGiaoHang.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
//            ttghRepo.save(thongTinGiaoHang);
//            return ResponseEntity.ok("Update thành công ");
//        } else {
//            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
//        }

        Optional<ThongTinGiaoHang> optionalThongTinGiaoHang = ttghRepo.findById(id);
        if (optionalThongTinGiaoHang.isPresent()) {

            ThongTinGiaoHang thongTinGiaoHangUpdate = thongTinGiaoHangRequest.toEntity();
            thongTinGiaoHangRequest.setId(id);
            thongTinGiaoHangUpdate.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
            thongTinGiaoHangUpdate.setNgayTao(optionalThongTinGiaoHang.get().getNgayTao());
            thongTinGiaoHangUpdate.setNgaySua(LocalDateTime.now());
            ThongTinGiaoHang saveThongTinGiaoHang = ttghRepo.save(thongTinGiaoHangUpdate);  // Lưu thay đổi và lấy đối tượng đã lưu
            return ResponseEntity.ok(saveThongTinGiaoHang);  // Trả về đối tượng đã cập nhật
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();  // Khởi tạo Map để trả về JSON hợp lệ
        if (ttghRepo.findById(id).isPresent()) {
            ttghRepo.deleteById(id);
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response);  // Trả về phản hồi JSON
        } else {
            response.put("message", "Không tìm thấy id cần xóa");
            return ResponseEntity.badRequest().body(response);  // Trả về phản hồi JSON khi lỗi
        }
    }


    @GetMapping("search")
    public ResponseEntity<?> searchByTenOrSdtNguoiNhan(
            @RequestParam(required = false) String tenNguoiNhan,
            @RequestParam(required = false) String sdtNguoiNhan) {

        List<ThongTinGiaoHangResponse> list = new ArrayList<>();
        ttghRepo.searchByTenOrSdtNguoiNhan(tenNguoiNhan, sdtNguoiNhan)
                .forEach(thongTin -> list.add(thongTin.toResponse()));

        if (list.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin giao hàng cần tìm kiếm.");
        }

        return ResponseEntity.ok(list);
    }
}
