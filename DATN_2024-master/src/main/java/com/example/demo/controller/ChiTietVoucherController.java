package com.example.demo.controller;

import com.example.demo.repository.ChiTietVoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("voucher")
public class ChiTietVoucherController {
    @Autowired
    private ChiTietVoucherRepository ctvcRepo;
//
//    @GetMapping
//    public ResponseEntity<?> findAllByKhachHang(){
//
//    }

}
