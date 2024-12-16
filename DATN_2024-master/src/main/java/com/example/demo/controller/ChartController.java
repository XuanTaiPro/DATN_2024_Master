package com.example.demo.controller;

import com.example.demo.repository.HoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("chart")
public class ChartController {

    @Autowired
    HoaDonRepo hoaDonRepo;

    @GetMapping("duLieu")
    public ResponseEntity<List<Map<String, Object>>> getTotalAmountAndNgayThanhToan(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer day) {

        List<Object[]> result = hoaDonRepo.getTotalAmountAndNgayThanhToan(year, month, week, day);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : result) {
            Map<String, Object> map = new HashMap<>();
            map.put("totalAmount", row[0]);
            map.put("ngayThanhToan", row[1]);
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<Integer>>> getFilters() {
        // Lấy năm hiện tại
        int currentYear = LocalDate.now().getYear();

        // Danh sách các năm (giả sử bạn muốn lấy 5 năm gần nhất và năm tiếp theo)
        List<Integer> years = Arrays.asList(currentYear - 2, currentYear - 1, currentYear, currentYear + 1,
                currentYear + 2);

        // Danh sách các tháng (từ 1 đến 12)
        List<Integer> months = Arrays.stream(Month.values())
                .map(Month::getValue)
                .collect(Collectors.toList());

        // Danh sách các tuần trong năm (giả sử tuần 1 đến tuần 52)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        List<Integer> weeks = new ArrayList<>();
        for (int i = 1; i <= 52; i++) {
            weeks.add(i);
        }

        // Danh sách các ngày trong tuần (1 đến 7)
        List<Integer> days = Arrays.asList(1, 2, 3, 4, 5, 6, 7); // Chủ nhật đến thứ bảy

        // Tạo Map chứa các bộ lọc
        Map<String, List<Integer>> filters = new HashMap<>();
        filters.put("years", years);
        filters.put("months", months);
        filters.put("weeks", weeks);
        filters.put("days", days);

        return ResponseEntity.ok(filters);
    }
}
