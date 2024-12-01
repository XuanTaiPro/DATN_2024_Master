package com.example.demo.controller;

import com.example.demo.dto.email.EmailRequest;
import com.example.demo.dto.khachhang.KhachHangRequestOnline;
import com.example.demo.entity.Account;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.GenerateCodeAll;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin("*")
@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private NhanVienRepository nvRepo;

    @Autowired
    private KhachHangRepository khRepo;

    @Autowired
    private GenerateCodeAll generateCodeAll;

    @Autowired
    private EmailService emailService;

    private Map<String, String> otpCache = new ConcurrentHashMap<>();

    public static String tenQuyen;

    public static String getIdNV;

    @PostMapping("manager")
    public ResponseEntity<?> loginNV(@Valid @RequestBody Account tk, BindingResult bindingResult, HttpSession ses) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        String email = tk.getEmail();
        NhanVien loginNV = nvRepo.loginNV(email, tk.getPassw());
        if (loginNV == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Tài khoản hoặc mật khẩu không đúng"));
        } else {
            String otp = genOtp(); // Sinh OTP
            otpCache.put("maOtp", otp);
            sendOtp(email, otp);

            tenQuyen = loginNV.getQuyen().getTen();
            getIdNV = loginNV.getId();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP đã được gửi tới email của bạn"));
        }
    }

    @GetMapping("getIdNV")
    public String getIdNV() {
        if (getIdNV != null) {
            return getIdNV;
        }
        return null;
    }

    @PostMapping("checkOtp")
    public ResponseEntity<?> checkOtp(@RequestBody Map<String, String> otpRequest, HttpSession ses) {
        String otp = otpRequest.get("otp");
        System.out.println(otpCache.get("maOtp") + " và " + otp);

        if (!otpCache.get("maOtp").equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mã OTP không chính xác"));
        }
        otpCache.remove("maOtp");
        System.out.println("otp : " + otp);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "redirectUrl",
                "http://localhost:63342/demo/src/main/webapp/ban_tai_quay/layout.html?_ijt=j7t7n918fk6aakf4tscjpovsb0#!/sanpham"));
    }

    @PostMapping("checkOtpOl")
    public ResponseEntity<?> checkOtpOl(@RequestBody Map<String, String> otpRequest, HttpSession ses) {
        String otp = otpRequest.get("otp");
        System.out.println(otpCache.get("maOtp") + " và " + otp);

        if (!otpCache.get("maOtp").equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mã OTP không chính xác"));
        }
        otpCache.remove("maOtp");
        System.out.println("otp : " + otp);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "redirectUrl", "https://youtube.com"));
    }

    private String genOtp() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private void sendOtp(String email, String otp) {
        EmailRequest emailRequest = new EmailRequest();

        emailRequest.setEmailNguoiNhan(email);
        emailRequest.setTieuDe("Mã xác nhận đăng nhập !!");
        emailRequest.setNoiDung("Mã xác nhận đăng nhập của bạn là : " + otp);

        try {
            emailService.sentEmail(email, "Mã xác nhận đăng nhập !!", "Mã xác nhận đăng nhập của bạn là : " + otp);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }

    }

    @PostMapping("onlineSale")
    public ResponseEntity<?> loginKH(@Valid @RequestBody Account tk, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        String email = tk.getEmail();
        KhachHang loginKH = khRepo.loginKH(email, tk.getPassw());
        if (loginKH == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Tài khoản hoặc mật khẩu không đúng"));
        } else {

            String otp = genOtp(); // Sinh OTP
            otpCache.put("maOtp", otp);
            sendOtp(email, otp);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP đã được gửi tới email của bạn"));
        }
    }

    @PostMapping("dangKy")
    public ResponseEntity<?> dangKy(@Valid @RequestBody KhachHangRequestOnline khachHangRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(String.join("\n", errors));
        }
        khachHangRequest.setId(Optional.ofNullable(khachHangRequest.getId())
                .filter(id -> !id.isEmpty())
                .orElse(UUID.randomUUID().toString().substring(0, 8).toUpperCase()));

        KhachHang khachHang = khachHangRequest.toEntity();
        khachHang.setNgayTao(LocalDateTime.now());
        khachHang.setMa(generateCodeAll.generateMaKhachHang());
        khachHang.setPassw(khachHangRequest.getPassw());
        khachHang.setEmail(khachHangRequest.getEmail());
        khachHang.setDiaChi(khachHangRequest.getDiaChi());
        khachHang.setGioiTinh(khachHangRequest.getGioiTinh());
        khachHang.setId(khachHangRequest.getId());
        khachHang.setTrangThai(1);
        khachHang.setSdt(khachHangRequest.getSdt());

        khRepo.save(khachHang);
        return ResponseEntity.ok("Thêm thành công");
    }
}
