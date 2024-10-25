package com.example.demo.controller;

import com.example.demo.repository.QuyenRepository;
import com.example.demo.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("quyen")
public class QuyenController {
    @Autowired
    private QuyenRepository qRepo;

    @GetMapping()
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(qRepo.findAll());
    }

    @GetMapping("getId")
    public String findQuyenByTen(@RequestParam String ten){
        return qRepo.getQuyenByTen(ten).getId();
    }
}
