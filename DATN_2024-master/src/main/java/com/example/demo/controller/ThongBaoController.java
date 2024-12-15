package com.example.demo.controller;

import com.example.demo.dto.thongbao.ThongBaoRequest;
import com.example.demo.dto.thongbao.ThongBaoResponse;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.ThongBaoRepository;
import com.example.demo.service.GenerateCodeAll;
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
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ThongBao> voucherPage = tbRepo.findAll(pageable);

        List<ThongBaoResponse> list = new ArrayList<>();
        voucherPage.forEach(c -> list.add(c.toResponse()));

        Map<String, Object> response = new HashMap<>();
        response.put("thongBaos", list);
        response.put("currentPage", voucherPage.getNumber());
        response.put("totalItems", voucherPage.getTotalElements());
        response.put("totalPages", voucherPage.getTotalPages());

        if (list.isEmpty()) {
            response.put("message", "Danh sách trống");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        if (tbRepo.findById(id).isPresent()) {
            return ResponseEntity.ok().body(tbRepo.findById(id).stream().map(ThongBao::toResponse));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy id để hiển thị");
        }
    }

    @PostMapping("add")
    public ResponseEntity<?> add( @RequestBody ThongBaoRequest thongBaoRequest) {
        if (thongBaoRequest.getId() == null || thongBaoRequest.getId().isEmpty()) {
            thongBaoRequest.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (thongBaoRequest.getMa() == null || thongBaoRequest.getMa().isEmpty()) {
            thongBaoRequest.setMa(generateCodeAll.generateMaThongBao());
        }

        ThongBao thongBao = thongBaoRequest.toEntity();
        List<KhachHang> khachHangs = khRepo.findAllById(thongBaoRequest.getIdKHs()); // Lấy danh sách khách hàng theo ID
        thongBao.setKhachHangs(khachHangs); // Gắn khách hàng vào thông báo
        thongBao.setNgayGui(LocalDateTime.now());
        thongBao.setNgayDoc(LocalDateTime.now());

        tbRepo.save(thongBao);
        return ResponseEntity.ok("Thêm thành công");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody ThongBaoRequest thongBaoRequest,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder mess = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> mess.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(mess.toString());
        }

        Optional<ThongBao> optionalThongBao = tbRepo.findById(id);
        if (optionalThongBao.isPresent()) {
            ThongBao thongBaoUpdate = thongBaoRequest.toEntity();
            thongBaoRequest.setId(id);

            // Cập nhật danh sách khách hàng cho thông báo
            List<KhachHang> khachHangs = khRepo.findAllById(thongBaoRequest.getIdKHs());
            thongBaoUpdate.setKhachHangs(khachHangs);

            thongBaoUpdate.setMa(optionalThongBao.get().getMa());
            thongBaoUpdate.setNgayGui(optionalThongBao.get().getNgayGui());
            thongBaoUpdate.setNgayDoc(optionalThongBao.get().getNgayDoc());

            ThongBao savedThongBao = tbRepo.save(thongBaoUpdate);
            return ResponseEntity.ok(savedThongBao);
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy thông báo cần cập nhật");
        }
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, String> response = new HashMap<>(); // Khởi tạo Map để trả về JSON hợp lệ
        if (tbRepo.findById(id).isPresent()) {
            tbRepo.deleteById(id);
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response); // Trả về phản hồi JSON
        } else {
            response.put("message", "Không tìm thấy id cần xóa");
            return ResponseEntity.badRequest().body(response); // Trả về phản hồi JSON khi lỗi
        }
    }
}
