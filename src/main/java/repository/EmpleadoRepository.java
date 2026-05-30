package com.example.gestionempleados.repository;

import com.example.gestionempleados.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Al heredar de JpaRepository, ya tienes métodos gratis como save(), findAll(), findById(), etc.
}