package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NhanVienRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private NhanVienRepository nvRepo;

    @Autowired
    private KhachHangRepository khRepo;


    @PostMapping("manager")
    public ResponseEntity<?> loginNV(@Valid @RequestBody Account tk, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        String email = tk.getEmail();
        NhanVien loginNV = nvRepo.loginNV(email, tk.getPassw());
        if(loginNV == null){
            return ResponseEntity.badRequest().body("k có dl");
        }else {
            return ResponseEntity.ok(loginNV);
        }
    }

    @PostMapping("onlineSale")
    public ResponseEntity<?> loginKH(@Valid @RequestBody Account tk,BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        String email = tk.getEmail();
        KhachHang loginKH = khRepo.loginKH(email, tk.getPassw());
        if(loginKH == null){
            return ResponseEntity.badRequest().body("k có dl");
        }else {
            return ResponseEntity.ok(loginKH);
        }
    }
}
