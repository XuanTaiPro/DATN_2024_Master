    package com.example.demo.controller;

    import com.example.demo.dto.hinhthuchoadon.HinhThucHoaDonRequest;
    import com.example.demo.entity.HinhThucHoaDon;
    import com.example.demo.entity.HoaDon;
    import com.example.demo.repository.HinhThucHoaDonRepo;
    import com.example.demo.repository.HoaDonRepo;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Optional;

    @CrossOrigin(origins = "*")
    @RestController
    @RequestMapping("hinh-thuc-hoa-don")
    public class HinhThucHoaDonController {
        @Autowired
        private HinhThucHoaDonRepo hinhThucHoaDonRepo;

        @Autowired
        private HoaDonRepo hoaDonRepo;

        // Thêm mới
        @PostMapping("/add")
        public ResponseEntity<String> addHinhThucHoaDon(@Valid @RequestBody HinhThucHoaDonRequest request) {
            try {
                HinhThucHoaDon entity = new HinhThucHoaDon();
                entity.setMaGiaoDich(request.getMaGiaoDich());

                // Chuyển đổi String sang LocalDate nếu cần
                entity.setNgayTao(request.getNgayTao());
                entity.setNgayThanhToan(request.getNgayThanhToan());
                entity.setSoTienKhachTra(request.getSoTienKhachTra());
                entity.setThanhTien(request.getThanhTien());
                entity.setNgayCapNhat(request.getNgayCapNhat());
                entity.setGhiChu(request.getGhiChu());
                entity.setTrangThai(request.getTrangThai());
                entity.setHinhThucThanhToan(request.getHinhThucThanhToan());

                // Kiểm tra sự tồn tại của hóa đơn
                Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(request.getIdHD()); // Thay đổi id thành idHD
                if (hoaDonOpt.isPresent()) {
                    entity.setHoaDon(hoaDonOpt.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hóa đơn không tồn tại");
                }

                hinhThucHoaDonRepo.save(entity);
                return ResponseEntity.status(HttpStatus.CREATED).body("Thêm mới thành công");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm mới thất bại: " + e.getMessage());
            }
        }

        // Cập nhật
        @PutMapping("/update/{id}")
        public ResponseEntity<String> updateHinhThucHoaDon(@PathVariable String id, @Valid @RequestBody HinhThucHoaDonRequest request) {
            Optional<HinhThucHoaDon> optionalHinhThucHoaDon = hinhThucHoaDonRepo.findById(id);

            if (optionalHinhThucHoaDon.isPresent()) {
                try {
                    HinhThucHoaDon entity = optionalHinhThucHoaDon.get();
                    entity.setMaGiaoDich(request.getMaGiaoDich());
                    entity.setNgayTao(request.getNgayTao());
                    entity.setNgayThanhToan(request.getNgayThanhToan());
                    entity.setSoTienKhachTra(request.getSoTienKhachTra());
                    entity.setThanhTien(request.getThanhTien());
                    entity.setNgayCapNhat(request.getNgayCapNhat());
                    entity.setGhiChu(request.getGhiChu());
                    entity.setTrangThai(request.getTrangThai());
                    entity.setHinhThucThanhToan(request.getHinhThucThanhToan());

                    // Kiểm tra sự tồn tại của hóa đơn
                    Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(request.getIdHD()); // Thay đổi id thành idHD
                    if (hoaDonOpt.isPresent()) {
                        entity.setHoaDon(hoaDonOpt.get());
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hóa đơn không tồn tại");
                    }

                    hinhThucHoaDonRepo.save(entity);
                    return ResponseEntity.ok("Cập nhật thành công");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hình thức hóa đơn");
            }
        }

        // Xóa
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<String> deleteHinhThucHoaDon(@PathVariable String id) {
            try {
                if (hinhThucHoaDonRepo.existsById(id)) {
                    hinhThucHoaDonRepo.deleteById(id);
                    return ResponseEntity.ok("Xóa thành công");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hình thức hóa đơn");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại: " + e.getMessage());
            }
        }

        @GetMapping("/list")
        public ResponseEntity<?> getHinhThucHoaDonList() {
            try {
                return ResponseEntity.ok(hinhThucHoaDonRepo.findAll());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lấy danh sách thất bại: " + e.getMessage());
            }
        }
    }
