package com.example.gestionempleados.model;
import java.time.LocalDate;

// "extends" es la clave para heredar los bienes de la abuela
public class Gerente extends Empleado {

    // private privado, doble el tipo de numero que soporta, pres... es el contenedor
    private double presupuestoArea;

    public Gerente (String numeroEmpleado, String nombre, String departamento, double salario,
                    LocalDate fechaIngreso, LocalDate fechaSalida, String funciones,
                    String jefeDirecto, double presupuestoArea) {

        // super llama al constructor de la clase base (Empleado)
        super(numeroEmpleado, nombre, departamento, salario, fechaIngreso, fechaSalida,  funciones, jefeDirecto);

        // guardamos el dato exclusivo del jefazo (clase)
        this.presupuestoArea = presupuestoArea; }

        // metodo exclusivo: solo el gerente tiene la facultad de autorizar dinero
        public void autorizarPresupuesto () {
            System.out.println("El Gerente " + getNombre() + " ha autorizado el presupuesto de: $" + presupuestoArea);
        }

    // Método exclusivo del Gerente
    public boolean autorizarAumento(Empleado empleado) {
        if (empleado.getAumentoPendiente() > 0) {
            double nuevoSalario = empleado.getSalario() + empleado.getAumentoPendiente();
            empleado.setSalario(nuevoSalario);
            empleado.setAumentoPendiente(0.0);
            return true;
        }
        return false;
    }
}
