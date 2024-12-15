package com.example.demo.controller;

import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangRequest;
import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangResponse;
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
            @RequestParam(defaultValue = "5") Integer size) {
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

    @GetMapping("detailByKhach/{id}")
    public ResponseEntity<?> detailByKhach(@PathVariable String id) {
        List<ThongTinGiaoHang> ttgh = ttghRepo.fHangs(id);
        if (ttgh == null || ttgh.size() == 0) {
            return ResponseEntity.badRequest().body("Không tìm được khách hàng");
        }

        return ResponseEntity.ok(ttgh.stream().map(ThongTinGiaoHang::toResponse));

    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody ThongTinGiaoHangRequest thongTinGiaoHangRequest,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }

        // khách hàng này đã có địa chỉ này rồi k thêm được
        if (ttghRepo.existsByKhachHang_IdAndDcNguoiNhan(thongTinGiaoHangRequest.getIdKH(),
                thongTinGiaoHangRequest.getDcNguoiNhan())) {
            return ResponseEntity.badRequest().body("Địa chỉ nhận hàng đã tồn tại cho khách hàng này.");
        }
        ThongTinGiaoHang thongTinGiaoHang = thongTinGiaoHangRequest.toEntity();
        thongTinGiaoHang.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
        thongTinGiaoHang.setNgayTao(LocalDateTime.now());
        thongTinGiaoHang.setTrangThai(1);
        ttghRepo.save(thongTinGiaoHang);
        return ResponseEntity.ok().body(ttghRepo.fHangs(thongTinGiaoHangRequest.getIdKH()));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @Valid @RequestBody ThongTinGiaoHangRequest thongTinGiaoHangRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        // if
        // (ttghRepo.existsByKhachHang_IdAndDcNguoiNhan(thongTinGiaoHangRequest.getIdKH(),
        // thongTinGiaoHangRequest.getDcNguoiNhan())) {
        // return ResponseEntity.badRequest().body("Địa chỉ nhận hàng đã tồn tại cho
        // khách hàng này.");
        // }
        // if (ttghRepo.findById(id).isPresent()) {
        // ThongTinGiaoHang thongTinGiaoHang = thongTinGiaoHangRequest.toEntity();
        // thongTinGiaoHang.setId(id);
        // thongTinGiaoHang.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
        // ttghRepo.save(thongTinGiaoHang);
        // return ResponseEntity.ok("Update thành công ");
        // } else {
        // return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        // }

        Optional<ThongTinGiaoHang> optionalThongTinGiaoHang = ttghRepo.findById(id);
        if (optionalThongTinGiaoHang.isPresent()) {

            ThongTinGiaoHang thongTinGiaoHangUpdate = thongTinGiaoHangRequest.toEntity();
            thongTinGiaoHangRequest.setId(id);
            thongTinGiaoHangUpdate.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
            thongTinGiaoHangUpdate.setNgayTao(optionalThongTinGiaoHang.get().getNgayTao());
            thongTinGiaoHangUpdate.setNgaySua(LocalDateTime.now());
            ThongTinGiaoHang saveThongTinGiaoHang = ttghRepo.save(thongTinGiaoHangUpdate); // Lưu thay đổi và lấy đối
            // tượng đã lưu
            return ResponseEntity.ok(saveThongTinGiaoHang); // Trả về đối tượng đã cập nhật
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
//        ThongTinGiaoHang ttgh = ttghRepo.getTTGHById(id);
//        if (ttgh != null) {
//            ThongTinGiaoHang newTTGH = ttghRepo.getTTGHById(id);
//            newTTGH.setTrangThai(0);
//            ttghRepo.save(newTTGH);
//            String idKH = ttghRepo.getCustomerIdByTTGHId(id);
//            return ResponseEntity.ok(ttghRepo.fHangs(idKH).stream().map(ThongTinGiaoHang::toResponse));
//        } else {
//            return ResponseEntity.badRequest().body("Không tìm thấy id cần xóa");
//        }
        if (ttghRepo.findById(id).isPresent()) {
            ttghRepo.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần xóa");
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
