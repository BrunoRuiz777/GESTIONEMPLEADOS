package com.example.gestionempleados.model;

import java.time.LocalDate;
import jakarta.persistence.*; // Importamos las herramientas de JPA

@Entity
@Table(name = "administradores_rh") // Su propia tabla en la base de datos
public class AdministradorRH extends Empleado {

    private String nivelPrivilegio;
    private String claveSistema;

    // CONSTRUCTOR VACÍO OBLIGATORIO PARA JPA
    public AdministradorRH() {
        super();
    }

    public AdministradorRH(String numeroEmpleado, String nombre, double salario,
                           LocalDate fechaIngreso, String nivelPrivilegio, String claveSistema) {

        super(numeroEmpleado, nombre, "Recursos Humanos", salario, fechaIngreso, null, "Administración de Personal", "Director de RH");
        this.nivelPrivilegio = nivelPrivilegio;
        this.claveSistema = claveSistema;
    }

    /**
     * MÉTODO DE EDICIÓN TOTAL
     * Permite al administrador modificar todos los datos editables de un empleado.
     */
    public void actualizarDatosEmpleado(Empleado empleado, String nuevoNombre, String nuevoDepto,
                                        double nuevoSalario, String nuevasFunciones, String nuevoJefe) {

        System.out.println(">> Solicitud de actualización de datos iniciada por: " + this.getNombre());

        // Verificación de seguridad por nivel de privilegio
        if (this.nivelPrivilegio.equals("ALTO")) {
            empleado.setNombre(nuevoNombre);
            empleado.setDepartamento(nuevoDepto);
            empleado.setSalario(nuevoSalario);
            empleado.setFunciones(nuevasFunciones);
            empleado.setJefeDirecto(nuevoJefe);
            System.out.println("✅ ÉXITO: Se han actualizado los datos de " + empleado.getNombre());
        } else {
            System.out.println("❌ DENEGADO: " + this.getNombre() + " no tiene nivel de privilegio ALTO para realizar cambios masivos.");
        }
    }

    // Métodos anteriores por si decides usarlos individualmente
    public void aplicarAumento(Empleado empleadoDestino, double porcentajeAumento) {
        if (this.nivelPrivilegio.equals("ALTO")) {
            double nuevoSalario = empleadoDestino.getSalario() * (1 + (porcentajeAumento / 100));
            empleadoDestino.setSalario(nuevoSalario);
            System.out.println("✅ Aumento del " + porcentajeAumento + "% applied.");
        }
    }

    public void modificarSalario(Empleado empleadoDestino, double nuevoSalario) {
        if (this.nivelPrivilegio.equals("ALTO")) {
            empleadoDestino.setSalario(nuevoSalario);
            System.out.println("✅ Salario modificado manualmente a $" + nuevoSalario + "");
        }
    }

    // Getters y Setters
    public String getNivelPrivilegio() { return nivelPrivilegio; }
    public void setNivelPrivilegio(String nivelPrivilegio) { this.nivelPrivilegio = nivelPrivilegio; }
    public String getClaveSistema() { return claveSistema; }
    public void setClaveSistema(String claveSistema) { this.claveSistema = claveSistema; }
}