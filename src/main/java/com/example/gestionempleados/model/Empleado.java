package com.example.gestionempleados.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import jakarta.persistence.*;

@Entity // Le dice a Spring "esta clase va a ser una tabla"
@Table(name = "empleados") // Opcional, pero le da un nombre bonito en plural a la tabla
@Inheritance(strategy = InheritanceType.JOINED)

public class Empleado {

    // 👇 AQUÍ ESTÁ LA LLAVE PRIMARIA QUE FALTABA
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le decimos a SQL que el número de empleado no se puede repetir
    @Column(unique = true, nullable = false)

    protected String numeroEmpleado;
    protected String nombre;
    protected String departamento;
    protected double salario;
    protected LocalDate fechaIngreso;
    protected LocalDate fechaSalida;
    protected String funciones;
    protected String jefeDirecto;
    private double aumentoPendiente = 0.0;

    public Empleado() {
    }

    public Empleado(String numeroEmpleado,
                    String nombre,
                    String departamento,
                    double salario,
                    LocalDate fechaIngreso,
                    LocalDate fechaSalida,
                    String funciones,
                    String jefeDirecto) {

        this.numeroEmpleado = numeroEmpleado;
        this.nombre = nombre;
        this.departamento = departamento;
        this.salario = salario;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.funciones = funciones;
        this.jefeDirecto = jefeDirecto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroEmpleado() { return numeroEmpleado; }
    public String getNombre() { return nombre; }
    public String getDepartamento() { return departamento; }
    public double getSalario() { return salario; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public String getFunciones() { return funciones; }
    public String getJefeDirecto() { return jefeDirecto; }

    public void setNumeroEmpleado(String numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public void setSalario(double salario) { this.salario = salario; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public void setFunciones(String funciones) { this.funciones = funciones; }
    public void setJefeDirecto(String jefeDirecto) { this.jefeDirecto = jefeDirecto; }

    public String getEstatus() {
        return (fechaSalida == null) ? "ACTIVO" : "INACTIVO";
    }

    public long getAniosTrabajados() {
        if (fechaIngreso == null) return 0;
        LocalDate fin = (fechaSalida == null) ? LocalDate.now() : fechaSalida;
        return ChronoUnit.YEARS.between(fechaIngreso, fin);
    }

    public int getAntiguedad() {
        if (fechaIngreso == null) return 0;

        LocalDate fin = (fechaSalida == null) ? LocalDate.now() : fechaSalida;

        return (int) ChronoUnit.YEARS.between(fechaIngreso, fin);
    }

    public int getVacaciones() {
        int anios = (int) getAniosTrabajados();

        if (anios <= 0) return 0;
        if (anios <= 5) return 10 + (anios * 2);

        return 20 + ((anios - 5) / 5) * 2;
    }
    public double getAumentoPendiente() {
        return aumentoPendiente;
    }

    public void setAumentoPendiente(double aumentoPendiente) {
        this.aumentoPendiente = aumentoPendiente;
    }
    public String getTipo() {
        return this.getClass().getSimpleName();
    }
}
