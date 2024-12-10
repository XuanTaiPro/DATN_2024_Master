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

                "redirectUrl", "http://localhost:63342/demo/src/main/webapp/ban_tai_quay/layout.html#!/sanpham"));
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

    @GetMapping("getmail")
    public ResponseEntity<?> getMail(@RequestParam(name = "email") String email) {
        if (khRepo.getKhachHangByEmail(email) != null) {
            String otp = genOtp(); // Sinh OTP
            otpCache.put("maOtp", otp);
            sendOtp(email, otp);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP đã được gửi tới email của bạn"));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "OTP đã được gửi tới email của bạn"));
    }

    @PostMapping("checkOtpFG")
    public ResponseEntity<?> checkOtpFG(@RequestBody Map<String, String> otpRequest, HttpSession ses) {
        String otp = otpRequest.get("otp");
        System.out.println(otpCache.get("maOtp") + " và " + otp);

        if (!otpCache.get("maOtp").equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mã OTP không chính xác"));
        }
        otpCache.remove("maOtp");
        System.out.println("otp : " + otp);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "OTP Chính xác"));
    }

    @PostMapping("changePW")
    public ResponseEntity<?> changePW(@RequestBody Map<String, String> passWordRequest) {
        String email = passWordRequest.get("email");
        String newPassW = passWordRequest.get("newPassW");
        String nhapLaiPassW = passWordRequest.get("nhapLaiPassW");

        if (!newPassW.equals(nhapLaiPassW)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mật khẩu không khớp, vui lòng nhập lại"
            ));
        }
        KhachHang khachHang = khRepo.getKhachHangByEmail(email);
        if (khachHang == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy khách hàng với email này"
            ));
        }
        khachHang.setPassw(newPassW);
        khachHang.setNgaySua(LocalDateTime.now());
        khRepo.save(khachHang);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "redirectUrl", "http://localhost:63342/demo/src/main/webapp/ban_tai_quay/view/loginOnline.html"
        ));
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
        khachHang.setTrangThai(1);

        khRepo.save(khachHang);

        String otp = genOtp();
        otpCache.put(khachHang.getEmail(), otp);
        try {
            sendOtp(khachHang.getEmail(), otp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi gửi email: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký thành công! OTP đã được gửi tới email của bạn"));
    }

    @PostMapping("checkOtpDK")
    public ResponseEntity<?> checkOtpDK(@RequestBody Map<String, String> otpRequest, HttpSession ses) {
        String otp = otpRequest.get("otp");
        System.out.println(otpCache.get("maOtp") + " và " + otp);

        if (!otpCache.get("maOtp").equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mã OTP không chính xác"));
        }
        otpCache.remove("maOtp");
        System.out.println("otp : " + otp);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "redirectUrl", "http://localhost:63342/demo/src/main/webapp/ban_tai_quay/view/loginOnline.html"));
    }

    public String returnIDNV() {
        return getIdNV;
    }


}
