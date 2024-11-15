package com.example.demo.controller;

import com.example.demo.entity.ChiTietVoucher;
import com.example.demo.repository.ChiTietVoucherRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("chi-tiet-voucher")
public class ChiTietVoucherController {
    @Autowired
    private ChiTietVoucherRepository ctvcRepo;

    @GetMapping("getByIdKhach/{id}")
    public ResponseEntity<?> findAllByKhachHang(@PathVariable String id) {
        if (id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy id khách hàng");
        }

        List<ChiTietVoucher> list = ctvcRepo.getByIdKhach(id);
        return ResponseEntity.ok().body(list);
    }

}
