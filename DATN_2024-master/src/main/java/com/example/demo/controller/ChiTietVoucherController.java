package com.example.demo.controller;

import com.example.demo.entity.Voucher;
import com.example.demo.repository.ChiTietVoucherRepository;

import java.util.ArrayList;
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

        List<String> list = ctvcRepo.getByIdKhach(id);

        List<Voucher> voucherList = new ArrayList<>();

        for (String idVC : list) {
            voucherList.add(ctvcRepo.findById(idVC).get().getVoucher());
        }

        return ResponseEntity.ok().body(voucherList);
    }

}
