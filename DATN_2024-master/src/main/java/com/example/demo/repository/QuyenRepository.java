package com.example.demo.repository;

import com.example.demo.entity.Quyen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuyenRepository extends JpaRepository<Quyen,String> {
    Quyen findByTen(String ten);
    Quyen getById(String id);
    Quyen getQuyenByTen(String ten);
}
