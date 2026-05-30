# 🏢 Sistema Integral de Gestión de Empleados

Una aplicación Fullstack desarrollada en Java y Spring Boot para la administración de recursos humanos. El sistema maneja distintos niveles de jerarquía corporativa (Administradores, Gerentes, Becarios y Empleados regulares), integrando control de sesiones y una arquitectura de base de datos relacional avanzada.

## 🚀 Tecnologías y Herramientas
* **Backend:** Java, Spring Boot 3
* **Persistencia:** Spring Data JPA, Hibernate
* **Base de Datos:** MySQL
* **Frontend:** Thymeleaf, HTML5, CSS3 puro (Diseño Responsivo)
* **Arquitectura:** Patrón MVC (Modelo-Vista-Controlador)

## ✨ Características Principales (Features)

* **Diseño de Base de Datos Avanzado (ORM):** Implementación de la estrategia `@Inheritance(strategy = InheritanceType.JOINED)` de JPA. Esto permite tener una base de datos perfectamente normalizada donde los datos compartidos viven en la tabla principal y los datos exclusivos viven en tablas satélites vinculadas automáticamente mediante llaves foráneas.
* **Seguridad y Control de Sesiones:** Uso de `HttpSession` para diferenciar las vistas y permisos entre un Administrador de RH (acceso total) y un Empleado/Gerente (acceso de solo lectura y flujos de aprobación).
* **Lógica Orientada a Objetos:** Aplicación práctica de herencia y polimorfismo. Por ejemplo, la interfaz gráfica invoca dinámicamente métodos exclusivos de la clase `Gerente` (como autorizar aumentos salariales) sin romper la estructura general de los empleados.
* **Operaciones CRUD:** Creación, lectura y actualización de registros persistentes en tiempo real hacia MySQL.

## ⚙️ Instalación y Configuración local

Si deseas clonar y correr este proyecto en tu entorno local, sientete libre