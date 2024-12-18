package com.example.demo.controller;

import com.example.demo.entity.Voucher;
import com.example.demo.repository.ChiTietVoucherRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("chi-tiet-voucher")
public class ChiTietVoucherController {
    @Autowired
    private ChiTietVoucherRepository ctvcRepo;

    @GetMapping("getByIdKhach")
    public ResponseEntity<?> findAllByKhachHang(@RequestParam String idKh, @RequestParam(required = false) String gia) {
        if (idKh.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy id khách hàng");
        }
        Integer giaInt = null;
        if (gia != null && !gia.trim().isEmpty()) {
            try {
                giaInt = Integer.parseInt(gia);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Giá phải là số");
            }
        }

        List<String> list = ctvcRepo.getByIdKhach(idKh);

        List<Voucher> voucherList = new ArrayList<>();

        for (String idVC : list) {
            Voucher vc = ctvcRepo.findById(idVC).get().getVoucher();

            if (giaInt != null) {
                Integer giaMin = Integer.parseInt(vc.getGiamMin());
                if (giaInt >= giaMin) {
                    voucherList.add(vc);
                }
            } else {
                voucherList.add(vc);
            }
        }

        return ResponseEntity.ok().body(voucherList);
    }
}
