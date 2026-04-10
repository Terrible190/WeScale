/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.repository;

/**
 *
 * @author Anas
 */
import java.util.List;
import com.example.demo.api.model.Personaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersonajeRepository extends JpaRepository<Personaje, Integer> {

    List<Personaje> findBySeleccionableTrue();
}