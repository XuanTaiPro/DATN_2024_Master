package com.example.demo.controller;

import com.example.demo.dto.danhgia.DanhGiaRequest;
import com.example.demo.dto.danhgia.DanhGiaRespOnline;
import com.example.demo.dto.danhgia.DanhGiaRespone;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.DanhGia;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.ChiTietSanPhamRepository;
import com.example.demo.repository.DanhGiaRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.SanPhamRepository;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("danh-gia")
public class DanhGiaController {
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private SanPhamRepository spRepo;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayDanhGia");
        List<DanhGia> danhGiaList = danhGiaRepository.findAll(sort);
        return ResponseEntity.ok(danhGiaList.stream().map(DanhGia::toRespone));
    }

    @GetMapping("listNoDone")
    public ResponseEntity<?> getNoDone(@RequestParam("idKH") String idKH) {
        if (khachHangRepository.findById(idKH).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy khách hàng.");
        }

        List<DanhGia> danhGias = danhGiaRepository.getByIdKH(idKH);

        if (danhGias.isEmpty()) {
            return ResponseEntity.ok().body(danhGias);
        }

        List<DanhGia> danhGiasNoDone = danhGias.stream().filter(dg -> dg.getTrangThai() == 0).toList();

        if (danhGiasNoDone.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        List<DanhGiaRespOnline> listDGO = new ArrayList<>();

        for (DanhGia dg : danhGiasNoDone) {
            DanhGiaRespOnline dgOnline = dg.convertFromDanhGia();
            dgOnline.setTrangThai(0);

            SanPham getSP = spRepo.findById(dg.getChiTietSanPham().getSanPham().getId()).get();
            dgOnline.setSp(getSP);

            listDGO.add(dgOnline);
        }

        return ResponseEntity.ok().body(listDGO);
    }

    @GetMapping("listDone")
    public ResponseEntity<?> getDone(@RequestParam("idKH") String idKH) {
        if (khachHangRepository.findById(idKH).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy khách hàng.");
        }

        List<DanhGia> danhGias = danhGiaRepository.getByIdKH(idKH);

        if (danhGias.isEmpty()) {
            return ResponseEntity.ok().body(danhGias);
        }

        List<DanhGia> danhGiasDone = danhGias.stream().filter(dg -> dg.getTrangThai() == 1).toList();

        if (danhGiasDone.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        List<DanhGiaRespOnline> listDGO = new ArrayList<>();

        for (DanhGia dg : danhGiasDone) {
            DanhGiaRespOnline dgOnline = dg.convertFromDanhGia();
            dgOnline.setTrangThai(1);
            dgOnline.setSao(dg.getSao());
            dgOnline.setNhanXet(dg.getNhanXet());

            SanPham getSP = spRepo.findById(dg.getChiTietSanPham().getSanPham().getId()).get();
            dgOnline.setSp(getSP);

            listDGO.add(dgOnline);
        }

        return ResponseEntity.ok().body(listDGO);
    }

    @GetMapping("/detailByKhach")
    public ResponseEntity<?> detailByKhach(@RequestParam String idkh) {
        if (khachHangRepository.findById(idkh).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy khách hàng.");
        }

        List<DanhGia> list = danhGiaRepository.getByIdKH(idkh);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/detailByCTSP")
    public ResponseEntity<?> detailByCTSP(@RequestParam String idCTSP) {
        if (chiTietSanPhamRepository.findById(idCTSP).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm.");
        }

        List<DanhGia> list = danhGiaRepository.getByIdCTSP(idCTSP);

        if (list.size() == 0) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        List<DanhGiaRespone> listDG = new ArrayList<DanhGiaRespone>();
        for (DanhGia dg : list) {
            listDG.add(dg.toRespone());
        }

        return ResponseEntity.ok().body(listDG);
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        DanhGia danhGia = danhGiaRepository.findById(id).orElse(null);
        if (danhGia != null) {
            return ResponseEntity.ok(danhGia.toRespone());
        }
        return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
    }

    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayDanhGia"));
        return ResponseEntity.ok(danhGiaRepository.findAll(pageRequest).stream().map(DanhGia::toRespone));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody DanhGiaRequest danhGiaRequest) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(danhGiaRequest.getIdCTSP()).orElse(null);
        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest()
                    .body("Không tìm thấy chi tiết sản phẩm có id: " + danhGiaRequest.getIdCTSP());
        }
        KhachHang khachHang = khachHangRepository.findById(danhGiaRequest.getIdKH()).orElse(null);
        if (khachHang == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy khách hàng có id: " + danhGiaRequest.getIdKH());
        }

        DanhGia existingDG = danhGiaRepository.getDGbySPandKH(danhGiaRequest.getIdCTSP(), danhGiaRequest.getIdKH());

        if (existingDG != null) {
            return ResponseEntity.badRequest().body("Khách hàng đã đánh giá cho sản phẩm này rồi");
        }

        DanhGia danhGia = new DanhGia();
        BeanUtils.copyProperties(danhGiaRequest, danhGia);
        danhGia.setChiTietSanPham(chiTietSanPham);
        danhGia.setKhachHang(khachHang);
        danhGia.setNgayDanhGia(LocalDateTime.now());
        danhGia.setNgaySua(null);
        danhGiaRepository.save(danhGia);
        return ResponseEntity.ok("Add done!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, String> evalMap) {
        String id = evalMap.get("idDG");

        DanhGia existingDG = danhGiaRepository.findById(id).orElse(null);
        if (existingDG == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
        }

        String sao = evalMap.get("sao");

        try {
            int soSao = Integer.parseInt(sao);
            if (soSao < 1 || soSao > 5) {
                return ResponseEntity.badRequest().body("Đánh giá phải trong khoảng từ 1 đến 5");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Đánh giá phải là số");
        }

        existingDG.setSao(Integer.parseInt(sao));

        String nhanXet = evalMap.get("nhanXet");

        if (nhanXet.length() > 255) {
            return ResponseEntity.badRequest().body("Nhận xét quá 255 ký tự");
        }

        existingDG.setNhanXet(nhanXet.trim());

        existingDG.setNgaySua(LocalDateTime.now());

        existingDG.setTrangThai(1);

        danhGiaRepository.save(existingDG);

        List<DanhGia> danhGias = danhGiaRepository.getByIdKH(existingDG.getKhachHang().getId());

        List<DanhGia> danhGiasNoDone = danhGias.stream().filter(dg -> dg.getTrangThai() == 0).toList();

        if (danhGiasNoDone.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        List<DanhGiaRespOnline> listDGO = new ArrayList<>();

        for (DanhGia dg : danhGiasNoDone) {
            DanhGiaRespOnline dgOnline = dg.convertFromDanhGia();
            dgOnline.setTrangThai(0);

            SanPham getSP = spRepo.findById(dg.getChiTietSanPham().getSanPham().getId()).get();
            dgOnline.setSp(getSP);

            listDGO.add(dgOnline);
        }

        return ResponseEntity.ok().body(listDGO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (danhGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
        }
        danhGiaRepository.deleteById(id);
        return ResponseEntity.ok("Xóa đánh giá thành công!");
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
