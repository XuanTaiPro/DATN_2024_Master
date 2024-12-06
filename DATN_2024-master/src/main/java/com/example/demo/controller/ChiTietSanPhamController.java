package com.example.demo.controller;

import com.example.demo.dto.chitietsanpham.ChiTietSanPhamRequest;
import com.example.demo.dto.chitietsanpham.ChiTietSanPhamResponse;
import com.example.demo.entity.AnhCTSP;
import com.example.demo.entity.ChiTietSanPham;
import com.example.demo.entity.LoHang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.*;
import com.example.demo.service.ChiTietSanPhamService;
import com.example.demo.service.GenerateCodeAll;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("chi-tiet-san-pham")
public class ChiTietSanPhamController {
    @Autowired
    SanPhamRepository sanPhamRepository;

    @Autowired
    GiamGiaRepository giamGiaRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    AnhCTSPRepository anhCTSPRepository;

    @Autowired
    DanhGiaRepository danhGiaRepository;

    @Autowired
    LoHangRepository lHRepo;

    @Autowired
    GenerateCodeAll genMa;

    @Autowired
    ChiTietSanPhamService ctspService;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findAll();
        if (chiTietSanPhams.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về 204 No Content nếu không có sản phẩm nào
        }
        List<ChiTietSanPhamResponse> responseList = chiTietSanPhams.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getAllCTSP")
    public ResponseEntity<?> getAllCTSP(@RequestParam(name = "idSP", required = false) String idSP) {
        List<ChiTietSanPham> listCTSP = chiTietSanPhamRepository.getAllByIdSPHD(idSP, 1);
        List<ChiTietSanPhamResponse> responseList = listCTSP.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/page")
    public ResponseEntity<?> page(@RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "idSP", required = false) String idSP) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("ngayTao")));
        Page<ChiTietSanPham> chiTietSanPhamPage = chiTietSanPhamRepository.getAllByIdSP(idSP, pageRequest);
        List<ChiTietSanPhamResponse> responseList = chiTietSanPhamPage.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("content", responseList);
        response.put("totalPages", chiTietSanPhamPage.getTotalPages());
        response.put("totalElements", chiTietSanPhamPage.getTotalElements());
        response.put("pageSize", chiTietSanPhamPage.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam String id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (chiTietSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy CTSP có id: " + id);
        }
        // List<LoHang> listLoHang=lHRepo.fByIdCTSP(id);
        return ResponseEntity.ok(chiTietSanPhamRepository.findById(id)
                .stream().map(ChiTietSanPham::toChiTietSanPhamResponse).findFirst().orElse(null));
    }

    @GetMapping("/detailByMa")
    public ResponseEntity<?> detailByMa(@RequestBody Map<String, String> request) {
        String ma = request.get("ma");
        if (ma == null || ma.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã không được để trống.");
        }
        if (!Pattern.matches("^CTSP[A-Z0-9]{6}$", ma.trim())) {
            return ResponseEntity.badRequest().body("Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!");
        }
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getByMa(ma);
        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy CTSP có mã: " + ma);
        }

        ChiTietSanPhamResponse response = chiTietSanPham.toChiTietSanPhamResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest) {
        // Chuẩn hóa số ngày sử dụng
        chiTietSanPhamRequest.setSoNgaySuDung(chiTietSanPhamRequest.getSoNgaySuDung().trim());

        LocalDateTime dateNow = LocalDateTime.now();

        String validationError = ctspService.validateRequest(chiTietSanPhamRequest, dateNow);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        String generatedMa;
        do {
            generatedMa = genMa.generateMa("CTSP-", 5);
        } while (chiTietSanPhamRepository.getByMa(generatedMa) != null);

        // Kiểm tra sản phẩm đã tồn tại
        ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.trungCTSP(
                chiTietSanPhamRequest.getIdSP(),
                chiTietSanPhamRequest.getSoNgaySuDung());

        // Cập nhật số lượng nếu sản phẩm đã tồn tại
        if (existingChiTietSanPham != null) {
            ctspService.updateExistingProduct(existingChiTietSanPham, chiTietSanPhamRequest);
            return ResponseEntity.ok("Sản phẩm đã tồn tại, số lượng đã được cập nhật!");
        }

        // Tạo mới chi tiết sản phẩm
        ChiTietSanPham newChiTietSanPham = ctspService.createNewProduct(generatedMa, chiTietSanPhamRequest);
        chiTietSanPhamRepository.save(newChiTietSanPham);

        ctspService.createNewLoHang(newChiTietSanPham, chiTietSanPhamRequest);

        if (chiTietSanPhamRequest.getLinkAnhList() != null && !chiTietSanPhamRequest.getLinkAnhList().isEmpty()) {
            for (String link : chiTietSanPhamRequest.getLinkAnhList()) {
                AnhCTSP anhCTSP = new AnhCTSP();
                anhCTSP.setLink(link);
                anhCTSP.setTrangThai(1);
                anhCTSP.setNgayTao(LocalDateTime.now());
                anhCTSP.setTen("Ảnh của" + newChiTietSanPham.getMa());
                anhCTSP.setChiTietSanPham(newChiTietSanPham); // Thiết lập liên kết với chi tiết sản phẩm
                anhCTSPRepository.save(anhCTSP); // Lưu ảnh vào repository
            }
        }

        return ResponseEntity.ok("Thêm mới chi tiết sản phẩm và hình ảnh thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @ModelAttribute ChiTietSanPhamRequest chiTietSanPhamRequest) {
        String id = chiTietSanPhamRequest.getId();
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.findById(id).orElse(null);
        if (existingChiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
        }

        LocalDateTime dateNow = LocalDateTime.now();

        String validationError = ctspService.validateRequest(chiTietSanPhamRequest, dateNow);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        if (chiTietSanPhamRequest.getListLoHang() != null) {
            List<Map<String, Object>> listLH = chiTietSanPhamRequest.getListLoHang();

            for (Map<String, Object> loHang : listLH) {

                LoHang getLoHang = lHRepo.findById((String) loHang.get("idLoHang")).get();

                // check số lượng để update
                Object sLuong = loHang.get("soLuong");
                Integer sLuongInt;
                if (sLuong instanceof Integer) {
                    sLuongInt = (Integer) sLuong;
                } else {
                    try {
                        sLuongInt = Integer.parseInt((String) loHang.get("soLuong"));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body("Số lượng phải là số.");
                    }
                }

                boolean checkDifferentSL = getLoHang.getSoLuong() != sLuongInt;

                if (checkDifferentSL) {
                    getLoHang.setSoLuong(sLuongInt);
                }

                // check hsd và nsx

                LocalDateTime hsdRequest = LocalDateTime.parse((String) loHang.get("hsd")).truncatedTo(ChronoUnit.DAYS);
                LocalDateTime hsdGetDataBase = getLoHang.getHsd().truncatedTo(ChronoUnit.DAYS);
                boolean checkDifferentHSD = hsdRequest.isEqual(hsdGetDataBase);
                if (!checkDifferentHSD) {
                    getLoHang.setHsd(LocalDateTime.parse((String) loHang.get("hsd")));
                }

                LocalDateTime nsxRequest = LocalDateTime.parse((String) loHang.get("nsx")).truncatedTo(ChronoUnit.DAYS);
                LocalDateTime nsxGetDataBase = getLoHang.getNsx().truncatedTo(ChronoUnit.DAYS);
                boolean checkDifferentNSX = nsxRequest.isEqual(nsxGetDataBase);

                if (!checkDifferentNSX) {
                    getLoHang.setNsx(LocalDateTime.parse((String) loHang.get("nsx")));
                }

                if (!checkDifferentSL || checkDifferentHSD || checkDifferentNSX) {
                    lHRepo.save(getLoHang);
                }
            }
        }

        // Xóa tất cả ảnh cũ nếu có yêu cầu thay đổi
        if (chiTietSanPhamRequest.getLinkAnhList() != null && !chiTietSanPhamRequest.getLinkAnhList().isEmpty()) {
            existingChiTietSanPham.getAnhCTSP().clear(); // Xóa ảnh cũ
            for (String link : chiTietSanPhamRequest.getLinkAnhList()) {
                AnhCTSP anhCTSP = new AnhCTSP();
                anhCTSP.setLink(link);
                anhCTSP.setTrangThai(1);
                anhCTSP.setNgayTao(LocalDateTime.now());
                anhCTSP.setTen("Ảnh của " + existingChiTietSanPham.getMa());
                anhCTSP.setChiTietSanPham(existingChiTietSanPham);
                anhCTSPRepository.save(anhCTSP);
                existingChiTietSanPham.getAnhCTSP().add(anhCTSP);
            }
        }

        if (chiTietSanPhamRequest.getLinkAnhList() == null) {
            existingChiTietSanPham.getAnhCTSP().clear(); // Xóa ảnh cũ
        }

        BeanUtils.copyProperties(chiTietSanPhamRequest, existingChiTietSanPham, "id", "ma", "ngayTao");
        existingChiTietSanPham.setNgaySua(LocalDateTime.now());
        chiTietSanPhamRepository.save(existingChiTietSanPham);

        return ResponseEntity.ok("Cập nhật chi tiết sản phẩm thành công!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }

        Optional<ChiTietSanPham> ctspObj = chiTietSanPhamRepository.findById(id);

        if (chiTietSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy CTSP có id: " + id);
        }

        ChiTietSanPham ctsp = ctspObj.get();
        ctsp.setTrangThai(0);
        chiTietSanPhamRepository.save(ctsp);

        return ResponseEntity.ok("Xóa thành công");
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
