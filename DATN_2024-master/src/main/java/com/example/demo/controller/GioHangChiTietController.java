package com.example.demo.controller;


import com.example.demo.dto.giohangchitiet.GioHangChiTietRequest;
import com.example.demo.dto.giohangchitiet.GioHangChiTietResponse;
import com.example.demo.entity.GioHangChiTiet;
import com.example.demo.repository.*;
import com.example.demo.service.GenerateCodeAll;
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

@RestController
@RequestMapping("giohangchitiet")
public class   GioHangChiTietController {
    @Autowired
    private GioHangChiTietRepository ghRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private ChiTietSanPhamRepository ctspRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<GioHangChiTietResponse> list = new ArrayList<>();
        ghRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("page")
    public ResponseEntity<?> page(@RequestParam(defaultValue = "0") Integer page) {
        Pageable p = PageRequest.of(page, 10);
        List<GioHangChiTietResponse> list = new ArrayList<>();
        ghRepo.findAll(p).forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        if (ghRepo.findById(id).isPresent()) {
            return ResponseEntity.ok().body(ghRepo.findById(id).stream().map(GioHangChiTiet::toResponse));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id để hiển thị");
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody GioHangChiTietRequest gioHangChiTietRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (gioHangChiTietRequest.getMa() == null || gioHangChiTietRequest.getMa().isEmpty()) {//nếu mã chưa đc điền thì tự động thêm mã
            gioHangChiTietRequest.setMa(generateCodeAll.generateMaGHCT());
        }
//        if (ghRepo.existsByMa(gioHangChiTietRequest.getMa())) {
//            return ResponseEntity.badRequest().body("mã đã tồn tại");
//        }
        if (gioHangChiTietRequest.getId() == null || gioHangChiTietRequest.getId().isEmpty()) {
            gioHangChiTietRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        GioHangChiTiet gioHangChiTiet = gioHangChiTietRequest.toEntity();
        gioHangChiTiet.setKhachHang(khRepo.getById(gioHangChiTietRequest.getIdKH()));
        gioHangChiTiet.setChiTietSanPham(ctspRepo.getById(gioHangChiTietRequest.getIdCTSP()));
        ghRepo.save(gioHangChiTiet);
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody GioHangChiTietRequest gioHangChiTietRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (ghRepo.findById(id).isPresent()) {
            GioHangChiTiet gioHangChiTiet = gioHangChiTietRequest.toEntity();
            gioHangChiTiet.setId(id);
            gioHangChiTiet.setKhachHang(khRepo.getById(gioHangChiTietRequest.getIdKH()));
            gioHangChiTiet.setChiTietSanPham(ctspRepo.getById(gioHangChiTietRequest.getIdCTSP()));
            ghRepo.save(gioHangChiTiet);
            return ResponseEntity.ok("Update thành công ");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (ghRepo.findById(id).isPresent()) {
            ghRepo.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần xóa");
        }
    }

    @GetMapping("sapXepNgaytao")
    public ResponseEntity<?> findAllSortedByNgayTao() {
        List<GioHangChiTietResponse> list = new ArrayList<>();
        ghRepo.findAllOrderByNgayTaoDesc().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }
}
