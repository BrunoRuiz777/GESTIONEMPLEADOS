package com.example.gestionempleados.service;

import com.example.gestionempleados.model.Empleado;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmpleadoService {

    private final List<Empleado> listaEmpleados = new ArrayList<>();

    // Ahora devuelve 'true' si fue exitoso, y 'false' si el ID ya existía
    public boolean agregar(Empleado empleado) {
        Empleado existente = buscarPorNumero(empleado.getNumeroEmpleado());

        if (existente == null) {
            listaEmpleados.add(empleado);
            return true; // Se guardó correctamente
        } else {
            return false; // Bloqueado por duplicado
        }
    }

    public List<Empleado> obtenerTodos() {
        return listaEmpleados;
    }

    public Empleado buscarPorNumero(String numero) {
        for (Empleado e : listaEmpleados) {
            if (e.getNumeroEmpleado().equals(numero)) {
                return e;
            }
        }
        return null;
    }
}