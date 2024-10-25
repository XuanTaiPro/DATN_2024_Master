package com.example.demo.controller;


import com.example.demo.dto.thongbao.ThongBaoRequest;
import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.ThongBaoRepository;
import com.example.demo.service.GenerateCodeAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@CrossOrigin("*")
@RestController
@RequestMapping("thongbao")
public class ThongBaoController {
    @Autowired
    private ThongBaoRepository tbRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<ThongBaoResponse> list = new ArrayList<>();
        tbRepo.findAll().forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("page")
    public ResponseEntity<?> page(@RequestParam(defaultValue = "0") Integer page) {
        Pageable p = PageRequest.of(page, 10);
        List<ThongBaoResponse> list = new ArrayList<>();
        tbRepo.findAll(p).forEach(c -> list.add(c.toResponse()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        if (tbRepo.findById(id).isPresent()) {
            return ResponseEntity.ok().body(tbRepo.findById(id).stream().map(ThongBao::toResponse));
        }else {
            return ResponseEntity.badRequest().body("Không tìm thấy id để hiển thị");
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody ThongBaoRequest thongBaoRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            System.out.println(mess.toString());
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (thongBaoRequest.getId() == null || thongBaoRequest.getId().isEmpty()) {
            thongBaoRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (thongBaoRequest.getMa() == null || thongBaoRequest.getMa().isEmpty()) {//nếu mã chưa đc điền thì tự động thêm mã
            thongBaoRequest.setMa(generateCodeAll.generateMaThongBao());
        }
//        if (tbRepo.existsByMa(thongBaoRequest.getMa())) {
//            return ResponseEntity.badRequest().body("mã đã tồn tại");
//        }
        ThongBao thongBao = thongBaoRequest.toEntity();
        thongBao.setKhachHang(khRepo.getById(thongBaoRequest.getIdKH()));
        thongBao.setNgayGui(LocalDateTime.now());
        tbRepo.save(thongBao);
        return ResponseEntity.ok("thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id,@Valid @RequestBody ThongBaoRequest thongBaoRequest,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
//        if (tbRepo.existsByMa(thongBaoRequest.getMa())) {
//            return ResponseEntity.badRequest().body("mã đã tồn tại");
//        }
        if (tbRepo.findById(id).isPresent()) {
            ThongBao thongBao = thongBaoRequest.toEntity();
            thongBao.setId(id);
            thongBao.setKhachHang(khRepo.getById(thongBaoRequest.getIdKH()));
            thongBao.setNgayGui(LocalDateTime.now());
            thongBao.setNgayDoc(LocalDateTime.now());
            tbRepo.save(thongBao);
            return ResponseEntity.ok("Update thành công ");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần update");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (tbRepo.findById(id).isPresent()) {
            tbRepo.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id cần xóa");
        }
    }
}
