package com.example.demo.controller;

import com.example.demo.dto.lichsuhoadon.LichSuHoaDonRequest;
import com.example.demo.dto.lichsuhoadon.LichSuHoaDonResponse;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.LichSuHoaDon;
import com.example.demo.repository.HoaDonRepo;
import com.example.demo.repository.LichSuHoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("lichsuhoadon")
public class LichSuHoaDonController {

    @Autowired
    private LichSuHoaDonRepo lichSuHoaDonRepo;

    @Autowired
    private HoaDonRepo hoaDonRepo;

    // Lấy danh sách lịch sử hóa đơn
    @GetMapping("/list")
    public ResponseEntity<List<LichSuHoaDonResponse>> getAllLichSuHoaDon() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<LichSuHoaDonResponse> lichSuHoaDonResponses = lichSuHoaDonRepo.findAll(sort)
                .stream()
                .map(LichSuHoaDon::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lichSuHoaDonResponses);
    }

    // Tìm kiếm lịch sử hóa đơn theo tên nhân viên hoặc mã hóa đơn
    @GetMapping("/search")
    public ResponseEntity<Page<LichSuHoaDonResponse>> searchByTenNVOrMaHD(@RequestParam("keyword") String keyword,
            Pageable pageable) {
        Page<LichSuHoaDon> searchResult = lichSuHoaDonRepo.searchByTenNVOrMaHD(keyword, pageable);
        Page<LichSuHoaDonResponse> responsePage = searchResult.map(LichSuHoaDon::toResponse);
        return ResponseEntity.ok(responsePage);
    }

    @PostMapping("/add")
    public ResponseEntity<LichSuHoaDonResponse> addLichSuHoaDon(
            @Validated @RequestBody LichSuHoaDonRequest lichSuHoaDonRequest) {
        // Kiểm tra hóa đơn tồn tại dựa trên idHD từ LichSuHoaDonRequest
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(lichSuHoaDonRequest.getIdHD());
        if (!hoaDonOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Tạo đối tượng LichSuHoaDon mới
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setId(lichSuHoaDonRequest.getId());
        lichSuHoaDon.setHoaDon(hoaDonOptional.get());
        lichSuHoaDon.setNgayTao(lichSuHoaDonRequest.getNgayTao());
        lichSuHoaDon.setTrangThai(lichSuHoaDonRequest.getTrangThai());

        // Lưu vào cơ sở dữ liệu
        LichSuHoaDon savedLichSuHoaDon = lichSuHoaDonRepo.save(lichSuHoaDon);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLichSuHoaDon.toResponse());
    }
}
