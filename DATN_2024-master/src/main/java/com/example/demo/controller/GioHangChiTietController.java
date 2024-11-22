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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("giohangchitiet")
public class GioHangChiTietController {
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

    @GetMapping("detailByIdKhach")
    public ResponseEntity<?> detailByIdKhach(@RequestParam(name = "idKhach") String id) {
        if (ghRepo.getAllByKhach(id) != null) {
            return ResponseEntity.ok().body(ghRepo.getAllByKhach(id).stream().map(gh -> gh.toOnline()));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy danh sach");
        }
    }

    @GetMapping("updateQuantity")
    public ResponseEntity<?> updateQuantity(@RequestParam(name = "id") String id,
            @RequestParam(name = "quantity") Integer quantity) {

        if (ghRepo.findById(id).isPresent()) {
            GioHangChiTiet gioHangChiTiet = ghRepo.findById(id).get();
            gioHangChiTiet.setSoLuong(quantity);
            ghRepo.save(gioHangChiTiet);

            return ResponseEntity.ok().build();
        } else {

            return ResponseEntity.badRequest().body("Không tìm thấy id để cập nhật");
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add(@Valid @RequestBody GioHangChiTietRequest gioHangChiTietRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }
        if (gioHangChiTietRequest.getMa() == null || gioHangChiTietRequest.getMa().isEmpty()) {// nếu mã chưa đc điền
                                                                                               // thì tự động thêm mã
            gioHangChiTietRequest.setMa(generateCodeAll.generateMaGHCT());
        }

        GioHangChiTiet gioHangChiTiet = gioHangChiTietRequest.toEntity();
        gioHangChiTiet.setKhachHang(khRepo.getById(gioHangChiTietRequest.getIdKH()));
        gioHangChiTiet.setChiTietSanPham(ctspRepo.getById(gioHangChiTietRequest.getIdCTSP()));
        ghRepo.save(gioHangChiTiet);
        return ResponseEntity.ok("thêm thành công");
    }

    @PostMapping("addOnline")
    public ResponseEntity<?> addOnline(@RequestBody GioHangChiTietRequest gh) {
        String idKh = gh.getIdKH();
        String idCTSP = gh.getIdCTSP();

        if (khRepo.findById(idKh) == null) {
            return ResponseEntity.badRequest().body("Không tìm được khách");
        }

        if (ctspRepo.findById(idCTSP) == null) {
            return ResponseEntity.badRequest().body("Không tìm được sản phẩm");
        }

        GioHangChiTiet getGH = ghRepo.getByIdKhachAndCTSP(idKh, idCTSP);
        if (getGH == null) {
            GioHangChiTiet ghChiTiet = gh.toEntity();

            ghChiTiet.setMa(generateCodeAll.generateMaGHCT());
            ghChiTiet.setTrangThai(1);
            ghChiTiet.setKhachHang(khRepo.findById(idKh).get());
            ghChiTiet.setChiTietSanPham(ctspRepo.findById(idCTSP).get());
            ghChiTiet.setNgayTao(String.valueOf(LocalDate.now()));

            ghRepo.save(ghChiTiet);

            return ResponseEntity.ok().body("Đã thêm");
        } else {
            getGH.setSoLuong(gh.getSoLuong() + getGH.getSoLuong());

            ghRepo.save(getGH);

            return ResponseEntity.ok().body("Đã cập nhật");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
            @Valid @RequestBody GioHangChiTietRequest gioHangChiTietRequest, BindingResult bindingResult) {
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
