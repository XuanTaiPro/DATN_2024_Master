package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService pService;

    @GetMapping("/vn-pay")
    public ResponseEntity<?> pay(HttpServletRequest request) {
        Map<String, Object> pdto = new HashMap<String, Object>();
        pdto.put("status", HttpStatus.OK);
        pdto.put("mess", "Thanh toán thành công");
        pdto.put("result", pService.createVnPayPayment(request));
        return ResponseEntity.ok(pdto);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<?> callback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        Map<String, Object> pdto = new HashMap<String, Object>();

        if (status.equals("00")) {
            pdto.put("status", HttpStatus.OK);
            pdto.put("mess", "Thanh toán thành công");
            // pdto.put("result", pService.createVnPayPayment(request));
            return ResponseEntity.ok(pdto);
        } else {
            pdto.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            pdto.put("mess", "Thanh toán thất bại");
            return ResponseEntity.badRequest().body(pdto);
        }
    }
}
