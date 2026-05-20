package com.example.demo.repository;

import com.example.demo.api.model.Accio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccioRepository extends JpaRepository<Accio, Integer> {
}