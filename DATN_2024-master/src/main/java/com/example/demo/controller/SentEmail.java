package com.example.demo.controller;

import com.example.demo.dto.email.EmailRequest;
import com.example.demo.dto.email.EmailRequestThongBao;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("mail")
public class SentEmail {

    @Autowired
    private EmailService emailService;

    @PostMapping("sentAllKH")
    public ResponseEntity<Map<String, String>> sentEmailsToAll(@RequestBody EmailRequestThongBao emailRequestThongBao) {
        Map<String, String> response = new HashMap<>();
        try {
            for (String email : emailRequestThongBao.getEmailNguoiNhan()) {
                emailService.sentEmail(email, emailRequestThongBao.getTieuDe(), emailRequestThongBao.getNoiDung());
            }
            response.put("message", "Email đã được gửi tới tất cả khách hàng!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Không thể gửi email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //    @PostMapping("sentKH")
//    public ResponseEntity<Map<String, String>> sentEmail(@RequestBody EmailRequest emailRequest) {
//        Map<String, String> response = new HashMap<>();
//        try {
//            emailService.sentEmail(
//                    emailRequest.getEmailNguoiNhan(),
//                    emailRequest.getTieuDe(),
//                    emailRequest.getNoiDung());
//            response.put("message", "Email đã gửi thành công!");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("message", "Không thể gửi email: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }



}
