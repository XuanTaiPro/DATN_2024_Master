package com.example.demo.controller;

import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangRequest;
import com.example.demo.dto.thongtingiaohang.ThongTinGiaoHangResponse;
import com.example.demo.entity.ThongTinGiaoHang;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.ThongTinGiaoHangRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    public ResponseEntity<?> page(@RequestParam(defaultValue = "0") Integer page) {
        Pageable p = PageRequest.of(page, 10);
        List<ThongTinGiaoHangResponse> list = new ArrayList<>();
        ttghRepo.findAll(p).forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
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
        if (ttghRepo.existsByKhachHang_IdAndDcNguoiNhan(thongTinGiaoHangRequest.getIdKH(), thongTinGiaoHangRequest.getDcNguoiNhan())) {
            return ResponseEntity.badRequest().body("Địa chỉ nhận hàng đã tồn tại cho khách hàng này.");
        }
        if (ttghRepo.findById(id).isPresent()) {
            ThongTinGiaoHang thongTinGiaoHang = thongTinGiaoHangRequest.toEntity();
            thongTinGiaoHang.setId(id);
            thongTinGiaoHang.setKhachHang(khRepo.getById(thongTinGiaoHangRequest.getIdKH()));
            ttghRepo.save(thongTinGiaoHang);
            return ResponseEntity.ok("Update thành công ");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (ttghRepo.findById(id).isPresent()) {
            ttghRepo.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
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
