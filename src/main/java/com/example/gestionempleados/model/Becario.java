package com.example.gestionempleados.model;

import java.time.LocalDate;
import jakarta.persistence.*; // Importamos las herramientas de JPA

@Entity
@Table(name = "becarios") // Su propia tabla en la base de datos
public class Becario extends Empleado {

    private String universidad;
    private int horasServicio;

    // CONSTRUCTOR VACÍO OBLIGATORIO PARA JPA
    public Becario() {
        super();
    }

    public Becario(String numeroEmpleado, String nombre, String departamento, double salario,
                   LocalDate fechaIngreso, LocalDate fechaSalida, String funciones,
                   String jefeDirecto, String universidad, int horasServicio) {

        // REUTILIZACIÓN: Mandamos los datos generales al padre (Empleado)
        super(numeroEmpleado, nombre, departamento, salario, fechaIngreso, fechaSalida, funciones, jefeDirecto);

        // ATRIBUTOS EXCLUSIVOS: Guardamos lo que es propio del becario
        this.universidad = universidad;
        this.horasServicio = horasServicio;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public int getHorasServicio() {
        return horasServicio;
    }

    public void setHorasServicio(int horasServicio) {
        this.horasServicio = horasServicio;
    }

    public void reportarHoras() {
        System.out.println( "El becario " + getNombre() + " de la universidad " + universidad + " ha reportado " + horasServicio + " horas." );
    }
}