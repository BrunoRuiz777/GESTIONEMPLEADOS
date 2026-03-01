package com.example.gestionempleados.controller;

import com.example.gestionempleados.model.AdministradorRH;
import com.example.gestionempleados.model.Empleado;
import com.example.gestionempleados.model.Becario;
import com.example.gestionempleados.model.Gerente;
import com.example.gestionempleados.service.EmpleadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // <-- NUEVA IMPORTACIÓN
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;

@Controller
public class HomeController {

    private final EmpleadoService empleadoService;

    // Instancia del Administrador de RH con privilegios "ALTO"
    private final AdministradorRH admin =
            new AdministradorRH("RH-01",
                    "Ana Gómez",
                    25000,
                    LocalDate.now(),
                    "ALTO",
                    "1234");

    public HomeController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    // ==========================
    // PÁGINA PRINCIPAL
    // ==========================
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Boolean isEmpleado = (Boolean) session.getAttribute("isEmpleado");
        Empleado empleadoActual = (Empleado) session.getAttribute("empleadoActual");

        // 🛟 SALVAVIDAS: Si la sesión se corrompió en un reinicio y el empleado es nulo,
        // forzamos el cierre de sesión simulado para evitar que Thymeleaf explote.
        if (empleadoActual == null) {
            isEmpleado = false;
            isAdmin = (isAdmin != null && isAdmin); // Mantenemos isAdmin si era true
        }

        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        model.addAttribute("isEmpleado", isEmpleado != null && isEmpleado);
        model.addAttribute("empleadoActual", empleadoActual);

        model.addAttribute("empleado", new Empleado());
        model.addAttribute("empleados", empleadoService.obtenerTodos());

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
    public String loginEmpleado(@RequestParam String idEmpleado,
                                HttpSession session) {

        Empleado emp = empleadoService.buscarPorNumero(idEmpleado);

        if (emp != null) {
            session.setAttribute("isEmpleado", true);
            session.setAttribute("isAdmin", false);
            session.setAttribute("empleadoActual", emp);
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
                                  RedirectAttributes redirectAttributes) { // <-- SE AGREGA ESTE PARÁMETRO

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin != null && isAdmin) {

            // 1. POLIMORFISMO: Creamos una variable de la clase Padre (Empleado)
            //            // que podrá guardar un Empleado, un Gerente o un Becario.
            Empleado empleadoFinal;
            // 2. LA DECISIÓN: Revisamos qué eligió el usuario en el formulario
            if (tipoEmpleado.equals("Gerente")) {
                //instanciamos la clase hija: Gerente
                empleadoFinal = new Gerente(
                        empleadoBase.getNumeroEmpleado(), empleadoBase.getNombre(),
                        empleadoBase.getDepartamento(), empleadoBase.getSalario(),
                        empleadoBase.getFechaIngreso(), empleadoBase.getFechaSalida(),
                        empleadoBase.getFunciones(), empleadoBase.getJefeDirecto(),
                        presupuestoArea); // atributo exclusivo del gerente

            } else if (tipoEmpleado.equals("Becario")) {
                //instanciamos la clase hija: Becario
                empleadoFinal = new Becario(
                        empleadoBase.getNumeroEmpleado(), empleadoBase.getNombre(),
                        empleadoBase.getDepartamento(), empleadoBase.getSalario(),
                        empleadoBase.getFechaIngreso(), empleadoBase.getFechaSalida(),
                        empleadoBase.getFunciones(), empleadoBase.getJefeDirecto(),
                        universidad, horasServicio); //atributos exclusivos
            } else {
                // si no es ninguno se queda como normal
                empleadoFinal = empleadoBase;
            }

            // guardamos el resultado
            boolean guardado = empleadoService.agregar(empleadoFinal);

            if (guardado) {
                redirectAttributes.addFlashAttribute("exito", "✅ " + tipoEmpleado + " registrado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "⚠ ALERTA: El ID ya existe. Registro cancelado.");
            }
        } // Cierra el if (isAdmin)

            return "redirect:/";
        } // Cierra el método agregarEmpleado

    // ==========================
    // ACTUALIZAR EMPLEADO (Solo Admin)
    // ==========================
    @PostMapping("/actualizarEmpleado")
    public String actualizarEmpleado(@RequestParam String idEmpleado,
                                     @RequestParam String nombre,
                                     @RequestParam String departamento,
                                     @RequestParam double salario,
                                     @RequestParam String funciones,
                                     @RequestParam String jefeDirecto,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) { // <-- SE AGREGA ESTE PARÁMETRO

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin != null && isAdmin) {
            Empleado emp = empleadoService.buscarPorNumero(idEmpleado);
            if (emp != null) {
                admin.actualizarDatosEmpleado(emp,
                        nombre,
                        departamento,
                        salario,
                        funciones,
                        jefeDirecto);

                // Mensaje visual al guardar una edición
                redirectAttributes.addFlashAttribute("exito", "💾 Datos actualizados correctamente.");
            }

        }

        return "redirect:/";
    }

    // ==========================================
    // FLUJO DE APROBACIÓN: SOLICITAR AUMENTO
    // ==========================================
    @PostMapping("/solicitarAumento")
    public String solicitarAumento(@RequestParam String idEmpleado,
                                   @RequestParam double montoAumento,
                                   RedirectAttributes redirectAttributes) {

        // 1. Buscamos a qué empleado le quieren subir el sueldo
        // (Nota: Asegúrate de usar el método que tengas en tu servicio para buscar por ID)
        Empleado empleado = empleadoService.buscarPorNumero(idEmpleado);

        if (empleado != null && montoAumento > 0) {
            // 2. Metemos el dinero al "bolsillo temporal"
            empleado.setAumentoPendiente(montoAumento);

            // 3. Guardamos los cambios (si tu método para actualizar se llama diferente, cámbialo aquí)
            //empleadoService.actualizar(empleado);

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
    public String autorizarAumento(@RequestParam String idEmpleado,
                                   @RequestParam String idGerente,
                                   RedirectAttributes redirectAttributes) {

        // Buscamos a los dos actores de esta historia
        Empleado empleado = empleadoService.buscarPorNumero(idEmpleado);
        Empleado gerenteActual = empleadoService.buscarPorNumero(idGerente);

        // Verificamos que el gerente que aprobó realmente sea de la clase Gerente
        if (empleado != null && gerenteActual instanceof Gerente) {

            // ¡Usamos el método exclusivo de la clase hija (Polimorfismo en acción)!
            boolean aprobado = ((Gerente) gerenteActual).autorizarAumento(empleado);

            if (aprobado) {
                redirectAttributes.addFlashAttribute("exito", "✅ ¡Aumento autorizado! El nuevo sueldo de " + empleado.getNombre() + " es de $" + empleado.getSalario());
            } else {
                redirectAttributes.addFlashAttribute("error", "⚠️ Este empleado no tenía aumentos pendientes.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "⛔ Error de permisos. Solo un Gerente puede autorizar aumentos.");
        }

        return "redirect:/";
    }
}