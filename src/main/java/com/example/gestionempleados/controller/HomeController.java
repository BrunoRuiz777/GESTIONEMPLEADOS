package com.example.gestionempleados.controller;

import com.example.gestionempleados.model.AdministradorRH;
import com.example.gestionempleados.model.Empleado;
import com.example.gestionempleados.model.Becario;
import com.example.gestionempleados.model.Gerente;
// Ya no usamos el EmpleadoService en memoria, usamos el Repositorio de BD
import com.example.gestionempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class HomeController {

    // Inyectamos la conexión directa a MySQL
    private final EmpleadoRepository empleadoRepository;

    // Instancia del Administrador de RH con privilegios "ALTO" (Este se queda en memoria como "Dios" del sistema)
    private final AdministradorRH admin =
            new AdministradorRH("RH-01",
                    "Ana Gómez",
                    25000,
                    LocalDate.now(),
                    "ALTO",
                    "1234");

    // Constructor para inyectar el repositorio
    public HomeController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    // ==========================
    // PÁGINA PRINCIPAL
    // ==========================
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Boolean isEmpleado = (Boolean) session.getAttribute("isEmpleado");
        Empleado empleadoActual = (Empleado) session.getAttribute("empleadoActual");

        // 🛟 SALVAVIDAS: Si la sesión se corrompió
        if (empleadoActual == null) {
            isEmpleado = false;
            isAdmin = (isAdmin != null && isAdmin);
        }

        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        model.addAttribute("isEmpleado", isEmpleado != null && isEmpleado);
        model.addAttribute("empleadoActual", empleadoActual);

        model.addAttribute("empleado", new Empleado());
        // Traemos todos los empleados reales desde la Base de Datos
        model.addAttribute("empleados", empleadoRepository.findAll());

        return "index";
    }

    // ==========================
    // LOGIN ADMIN
    // ==========================
    @PostMapping("/loginAdmin")
    public String loginAdmin(@RequestParam String password,
                             HttpSession session) {

        if ("1234".equals(password)) {
            session.setAttribute("isAdmin", true);
            session.setAttribute("isEmpleado", false);
            session.removeAttribute("empleadoActual");
        }
        return "redirect:/";
    }

    // ==========================
    // LOGIN EMPLEADO
    // ==========================
    @PostMapping("/loginEmpleado")
    public String loginEmpleado(@RequestParam Long idEmpleado, // Cambiado a Long por ser ID de BD
                                HttpSession session) {

        // Buscamos en la base de datos
        Optional<Empleado> emp = empleadoRepository.findById(idEmpleado);

        if (emp.isPresent()) {
            session.setAttribute("isEmpleado", true);
            session.setAttribute("isAdmin", false);
            session.setAttribute("empleadoActual", emp.get());
        }

        return "redirect:/";
    }

    // ==========================
    // LOGOUT
    // ==========================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ==========================
    // AGREGAR EMPLEADO (Solo Admin)
    // ==========================
    @PostMapping("/agregar")
    public String agregarEmpleado(@ModelAttribute Empleado empleadoBase,
                                  @RequestParam(required = false, defaultValue = "Normal") String tipoEmpleado,
                                  @RequestParam(required = false, defaultValue = "0.0") double presupuestoArea,
                                  @RequestParam(required = false, defaultValue = "") String universidad,
                                  @RequestParam(required = false, defaultValue = "0") int horasServicio,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin != null && isAdmin) {
            Empleado empleadoFinal;

            // POLIMORFISMO: Decidimos qué tipo de objeto crear
            if ("Gerente".equals(tipoEmpleado)) {
                empleadoFinal = new Gerente(
                        empleadoBase.getNumeroEmpleado(), empleadoBase.getNombre(),
                        empleadoBase.getDepartamento(), empleadoBase.getSalario(),
                        empleadoBase.getFechaIngreso(), null,
                        empleadoBase.getFunciones(), empleadoBase.getJefeDirecto(),
                        presupuestoArea);
            } else if ("Becario".equals(tipoEmpleado)) {
                empleadoFinal = new Becario(
                        empleadoBase.getNumeroEmpleado(), empleadoBase.getNombre(),
                        empleadoBase.getDepartamento(), empleadoBase.getSalario(),
                        empleadoBase.getFechaIngreso(), null,
                        empleadoBase.getFunciones(), empleadoBase.getJefeDirecto(),
                        universidad, horasServicio);
            } else {
                empleadoFinal = empleadoBase;
            }

            // Guardamos en MySQL usando JPA
            try {
                empleadoRepository.save(empleadoFinal);
                redirectAttributes.addFlashAttribute("exito", "✅ " + tipoEmpleado + " registrado correctamente en la Base de Datos.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "⚠ ALERTA: Error al guardar. Revisa que el número de empleado no esté repetido.");
            }
        }
        return "redirect:/";
    }

    // ==========================
    // ACTUALIZAR EMPLEADO (Solo Admin)
    // ==========================
    @PostMapping("/actualizarEmpleado")
    public String actualizarEmpleado(@RequestParam Long idEmpleado, // Cambiado a Long
                                     @RequestParam String nombre,
                                     @RequestParam String departamento,
                                     @RequestParam double salario,
                                     @RequestParam String funciones,
                                     @RequestParam String jefeDirecto,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin != null && isAdmin) {
            Optional<Empleado> optEmp = empleadoRepository.findById(idEmpleado);

            if (optEmp.isPresent()) {
                Empleado emp = optEmp.get();
                admin.actualizarDatosEmpleado(emp, nombre, departamento, salario, funciones, jefeDirecto);

                // Guardamos los cambios en MySQL
                empleadoRepository.save(emp);

                redirectAttributes.addFlashAttribute("exito", "💾 Datos actualizados correctamente en la Base de Datos.");
            }
        }

        return "redirect:/";
    }

    // ==========================================
    // FLUJO DE APROBACIÓN: SOLICITAR AUMENTO
    // ==========================================
    @PostMapping("/solicitarAumento")
    public String solicitarAumento(@RequestParam Long idEmpleado, // Cambiado a Long
                                   @RequestParam double montoAumento,
                                   RedirectAttributes redirectAttributes) {

        Optional<Empleado> optEmp = empleadoRepository.findById(idEmpleado);

        if (optEmp.isPresent() && montoAumento > 0) {
            Empleado empleado = optEmp.get();
            empleado.setAumentoPendiente(montoAumento);

            // Guardamos en MySQL
            empleadoRepository.save(empleado);

            redirectAttributes.addFlashAttribute("exito", "⏳ Solicitud enviada: Aumento de $" + montoAumento + " pendiente de autorización.");
        } else {
            redirectAttributes.addFlashAttribute("error", "⚠️ Error al solicitar el aumento. Verifica los datos.");
        }

        return "redirect:/";
    }

    // ==========================================
    // FLUJO DE APROBACIÓN: GERENTE AUTORIZA
    // ==========================================
    @PostMapping("/autorizarAumento")
    public String autorizarAumento(@RequestParam Long idEmpleado, // Cambiado a Long
                                   @RequestParam Long idGerente,  // Cambiado a Long
                                   RedirectAttributes redirectAttributes) {

        Optional<Empleado> optEmp = empleadoRepository.findById(idEmpleado);
        Optional<Empleado> optGerente = empleadoRepository.findById(idGerente);

        if (optEmp.isPresent() && optGerente.isPresent()) {
            Empleado empleado = optEmp.get();
            Empleado gerenteActual = optGerente.get();

            // Verificamos que sea un Gerente el que autoriza
            if (gerenteActual instanceof Gerente) {

                // Polimorfismo en acción
                boolean aprobado = ((Gerente) gerenteActual).autorizarAumento(empleado);

                if (aprobado) {
                    // Si se aprueba, guardamos los cambios de salario en MySQL
                    empleadoRepository.save(empleado);
                    redirectAttributes.addFlashAttribute("exito", "✅ ¡Aumento autorizado! El nuevo sueldo de " + empleado.getNombre() + " es de $" + empleado.getSalario());
                } else {
                    redirectAttributes.addFlashAttribute("error", "⚠️ Este empleado no tenía aumentos pendientes.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "⛔ Error de permisos. Solo un Gerente puede autorizar aumentos.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "⛔ Error al encontrar los datos del empleado o gerente.");
        }

        return "redirect:/";
    }
}