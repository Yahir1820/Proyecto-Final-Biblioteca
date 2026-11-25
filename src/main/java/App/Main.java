package App;

//dependencias necesarias para la gestión central.
import Excepciones.*;
import Materiales.*;
import Servicios.*;
import Usuarios.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal para operar el sistema de biblioteca.
 * Permite ejercitar todos los servicios, excepciones personalizadas, etc.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Biblioteca biblioteca = new Biblioteca("Biblioteca Universitaria Central");
    // instancia auxiliar utilizada para emitir comprobantes de texto de préstamos y devoluciones.
    private static final GeneradorComprobante comprobante = new GeneradorComprobante();

    /**
     * Punto de entrada de la aplicación. Carga datos de prueba iniciales y despliega el menú principal.
     * @param args Argumentos de línea de comandos (no se utilizan explícitamente en el sistema).
     */
    public static void main(String[] args) throws BibliotecaException {
        cargarDatosIniciales();

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    menuAcervo();
                    break;
                case 2:
                    menuUsuarios();
                    break;
                case 3:
                    menuCirculacion();
                    break;
                case 4:
                    menuReportes();
                    break;
                case 0:
                    System.out.println("\nCerrando el sistema bibliotecario. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("\n[Aviso] Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);

    }

    /**
     * Precarga registros representativos de cada jerarquía (usuarios, materiales y un préstamo)
     * para facilitar las pruebas inmediatas.
     */
    private static void cargarDatosIniciales() throws BibliotecaException {
            // Usuarios
            Bibliotecario bib1 = new Bibliotecario("USR-01", "Laura Méndez", "lmendez@uni.edu.mx", "EMP-101", "Matutino");
            Estudiante est1 = new Estudiante("USR-02", "Carlos Ramírez", "cramirez@estudiantes.uni.edu.mx", "S22014589", "Ingeniería de Software");
            Docente doc1 = new Docente("USR-03", "Dra. Elena Torres", "etorres@uni.edu.mx", "DOC-504", "Facultad de Ingeniería");

            biblioteca.registrarUsuario(bib1);
            biblioteca.registrarUsuario(est1);
            biblioteca.registrarUsuario(doc1);

            // Materiales
            MaterialBibliografico mat1 = new Libro("MAT-01", "Clean Code", "Robert C. Martin", 2008, 3, "978-0132350884", 464);
            MaterialBibliografico mat2 = new LibroConsulta("MAT-02", "Enciclopedia de Ciencias Computacionales", "Varios Autores", 2021, 2, "978-8499641234", 1250, AreaReferencia.ENCICLOPEDIA, "Sala A - Estante 2");
            MaterialBibliografico mat3 = new Revista("MAT-03", "IEEE Software", "IEEE Computer Society", 2025, 4, "0740-7459", 42);
            MaterialBibliografico mat4 = new Tesis("MAT-04", "Arquitecturas Orientadas a Objetos en Java", "Ana Sofía Gómez", 2024, 2, GradoAcademico.MAESTRIA, "Universidad Veracruzana");

            biblioteca.registrarMaterial(mat1);
            biblioteca.registrarMaterial(mat2);
            biblioteca.registrarMaterial(mat3);
            biblioteca.registrarMaterial(mat4);

            // Préstamo inicial
            Prestamo prestamoInicial = new Prestamo("FOL-100", est1, bib1, LocalDate.now().minusDays(5));
            prestamoInicial.agregarDetalle(mat1, 1);
            biblioteca.registrarPrestamo(prestamoInicial);
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n====================================================");
        System.out.println("   " + biblioteca.getNombre().toUpperCase());
        System.out.println("====================================================");
        System.out.println("1. Gestión de Acervo Bibliográfico");
        System.out.println("2. Gestión de Usuarios y Lectores");
        System.out.println("3. Préstamos y Devoluciones");
        System.out.println("4. Reportes generales");
        System.out.println("0. Salir");
        System.out.println("----------------------------------------------------");
    }

    // submenú 1: Gestión de acervo
    private static void menuAcervo() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE ACERVO BIBLIOGRÁFICO ---");
            System.out.println("1. Alta de nuevo material (RF-01)");
            System.out.println("2. Listar catálogo activo (RF-02)");
            System.out.println("3. Buscar material por ID o texto (RF-02)");
            System.out.println("4. Actualizar datos o stock de material (RF-03)");
            System.out.println("5. Dar de baja lógica un material (RF-04)");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Opción: ");

            try {
                switch (opcion) {
                    case 1:
                        registrarNuevoMaterial();
                        break;
                    case 2:
                        listarCatalogoActivo();
                        break;
                    case 3:
                        buscarMaterial();
                        break;
                    case 4:
                        actualizarMaterial();
                        break;
                    case 5:
                        darDeBajaMaterial();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("[Aviso] Opción inválida.");
                }
            } catch (BibliotecaException e) {
                // Captura reglas de negocio incumplidas sin detener el programa.
                System.out.println("\n[ERROR DE NEGOCIO] " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void registrarNuevoMaterial() throws BibliotecaException {
        System.out.println("\nTipo de material a registrar:");
        System.out.println("1. Libro general (Plazo: 7 días, Multa: $10/día)");
        System.out.println("2. Libro de consulta en sala (No prestable externo)");
        System.out.println("3. Revista (Plazo: 3 días, Multa: $5/día)");
        System.out.println("4. Tesis (Plazo: 5 días, Multa: $15/día)");
        int tipo = leerEntero("Seleccione tipo: ");

        String id = leerTexto("ID del material (ej. MAT-05): ");
        String titulo = leerTexto("Título: ");
        String autor = leerTexto("Autor / Editorial: ");
        int anio = leerEntero("Año de publicación: ");
        int ejemplares = leerEntero("Ejemplares disponibles: ");

        MaterialBibliografico nuevoMaterial;
        switch (tipo) {
            case 1:
                String isbn = leerTexto("ISBN: ");
                int paginas = leerEntero("Número de páginas: ");
                nuevoMaterial = new Libro(id, titulo, autor, anio, ejemplares, isbn, paginas);
                break;
            case 2:
                String isbnConsulta = leerTexto("ISBN: ");
                int paginasConsulta = leerEntero("Número de páginas: ");
                String area = leerTexto("Área de referencia: ");
                String sala = leerTexto("Ubicación en sala: ");
                nuevoMaterial = new LibroConsulta(id, titulo, autor, anio, ejemplares, isbnConsulta, paginasConsulta, AreaReferencia.valueOf(area), sala);
                break;
            case 3:
                String issn = leerTexto("ISSN: ");
                int edicion = leerEntero("Número de edición: ");
                nuevoMaterial = new Revista(id, titulo, autor, anio, ejemplares, issn, edicion);
                break;
            case 4:
                String grado = leerTexto("Grado académico (Licenciatura/Maestría/Doctorado): ");
                String institucion = leerTexto("Institución: ");
                nuevoMaterial = new Tesis(id, titulo, autor, anio, ejemplares, GradoAcademico.valueOf(grado), institucion);
                break;
            default:
                throw new BibliotecaException("Tipo de material no reconocido.");
        }

        biblioteca.registrarMaterial(nuevoMaterial);
        System.out.println("[Éxito] Material registrado correctamente: " + nuevoMaterial.mostrarFicha());
    }

    private static void listarCatalogoActivo() {
        List<MaterialBibliografico> activos = biblioteca.listarMaterialesActivos();
        System.out.println("\n--- CATÁLOGO DE MATERIALES ACTIVOS (" + activos.size() + ") ---");
        if (activos.isEmpty()) {
            System.out.println("No hay materiales activos registrados.");
            return;
        }
        // Se recorre la colección de la superclase invocando mostrarFicha() en cada subclase.
        for (MaterialBibliografico m : activos) {
            System.out.println(m.mostrarFicha());
        }
    }

    private static void buscarMaterial() throws BibliotecaException {
        System.out.println("\n1. Buscar por ID exacto");
        System.out.println("2. Buscar por palabra clave en título o autor");
        int modo = leerEntero("Seleccione modo: ");

        if (modo == 1) {
            String id = leerTexto("Ingrese ID del material: ");
            MaterialBibliografico m = biblioteca.buscarMaterialPorId(id);
            System.out.println("\nResultado:\n" + m.mostrarFicha());
        } else if (modo == 2) {
            String texto = leerTexto("Ingrese texto a buscar: ");
            List<MaterialBibliografico> resultados = biblioteca.buscarMaterialesPorTexto(texto);
            System.out.println("\nCoincidencias encontradas (" + resultados.size() + "):");
            for (MaterialBibliografico m : resultados) {
                System.out.println(m.mostrarFicha());
            }
        } else {
            System.out.println("[Aviso] Modo de búsqueda no válido.");
        }
    }

    private static void actualizarMaterial() throws BibliotecaException {
        String id = leerTexto("\nID del material a actualizar: ");
        MaterialBibliografico actual = biblioteca.buscarMaterialPorId(id);
        System.out.println("Datos actuales: " + actual.mostrarFicha());

        String nuevoTitulo = leerTexto("Nuevo título (Enter para conservar actual): ");
        String nuevoAutor = leerTexto("Nuevo autor (Enter para conservar actual): ");
        int nuevosEjemplares = leerEntero("Nueva cantidad de ejemplares disponibles: ");

        biblioteca.actualizarMaterial(id, nuevoTitulo, nuevoAutor, nuevosEjemplares);
        System.out.println("[Éxito] Material actualizado:\n" + actual.mostrarFicha());
    }

    private static void darDeBajaMaterial() throws BibliotecaException {
        String id = leerTexto("\nID del material a dar de baja lógica: ");
        biblioteca.darDeBajaMaterial(id);
        System.out.println("[Éxito] El material '" + id + "' ha sido marcado como inactivo.");
    }

    // submenú 2: Gestión de usuarios
    private static void menuUsuarios() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE USUARIOS ---");
            System.out.println("1. Alta de nuevo usuario (RF-05)");
            System.out.println("2. Listar usuarios activos (RF-06)");
            System.out.println("3. Consultar usuario por ID (RF-06)");
            System.out.println("4. Actualizar correo electrónico (RF-06)");
            System.out.println("5. Liquidar adeudo de multas de lector (RF-06)");
            System.out.println("6. Dar de baja lógica un usuario (RF-07)");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Opción: ");

            try {
                switch (opcion) {
                    case 1:
                        registrarNuevoUsuario();
                        break;
                    case 2:
                        listarUsuariosActivos();
                        break;
                    case 3:
                        String idBuscado = leerTexto("ID del usuario: ");
                        System.out.println(biblioteca.buscarUsuarioPorId(idBuscado));
                        break;
                    case 4:
                        String idCorreo = leerTexto("ID del usuario: ");
                        String nuevoCorreo = leerTexto("Nuevo correo electrónico: ");
                        biblioteca.actualizarCorreoUsuario(idCorreo, nuevoCorreo);
                        System.out.println("[Éxito] Correo actualizado correctamente.");
                        break;
                    case 5:
                        String idLector = leerTexto("ID del lector que liquida multa: ");
                        biblioteca.liquidarMultaLector(idLector);
                        System.out.println("[Éxito] Adeudo liquidado. El lector vuelve a ser elegible para préstamos.");
                        break;
                    case 6:
                        String idBaja = leerTexto("ID del usuario a dar de baja lógica: ");
                        biblioteca.darDeBajaUsuario(idBaja);
                        System.out.println("[Éxito] El usuario '" + idBaja + "' ha sido dado de baja lógica.");
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("[Aviso] Opción inválida.");
                }
            } catch (BibliotecaException e) {
                System.out.println("\n[ERROR DE NEGOCIO] " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void registrarNuevoUsuario() throws BibliotecaException {
        System.out.println("\nPerfil de usuario a registrar:");
        System.out.println("1. Estudiante (Límite: 3 materiales, +0 días extra)");
        System.out.println("2. Docente (Límite: 5 materiales, +3 días extra)");
        System.out.println("3. Bibliotecario (Personal operativo)");
        int tipo = leerEntero("Seleccione perfil: ");

        String id = leerTexto("ID de usuario (ej. USR-04): ");
        String nombre = leerTexto("Nombre completo: ");
        String correo = leerTexto("Correo electrónico: ");

        Usuario nuevoUsuario;
        switch (tipo) {
            case 1:
                String matricula = leerTexto("Matrícula: ");
                String carrera = leerTexto("Carrera: ");
                nuevoUsuario = new Estudiante(id, nombre, correo, matricula, carrera);
                break;
            case 2:
                String numPersonal = leerTexto("Número de personal: ");
                String facultad = leerTexto("Facultad: ");
                nuevoUsuario = new Docente(id, nombre, correo, numPersonal, facultad);
                break;
            case 3:
                String numEmpleado = leerTexto("Número de empleado: ");
                String turno = leerTexto("Turno (Matutino/Vespertino): ");
                nuevoUsuario = new Bibliotecario(id, nombre, correo, numEmpleado, turno);
                break;
            default:
                throw new BibliotecaException("Tipo de usuario no reconocido.");
        }

        biblioteca.registrarUsuario(nuevoUsuario);
        System.out.println("[Éxito] Usuario registrado: " + nuevoUsuario);
    }

    private static void listarUsuariosActivos() {
        List<Usuario> activos = biblioteca.listarUsuariosActivos();
        System.out.println("\n--- PADRÓN DE USUARIOS ACTIVOS (" + activos.size() + ") ---");
        // polimorfismo: invoca toString() y obtenerRol() sobre la colección de la clase base Usuario.
        for (Usuario u : activos) {
            System.out.println(u);
        }
    }

    // submenú 3: Préstamos y devoluciones
    private static void menuCirculacion() {
        int opcion;
        do {
            System.out.println("\n--- CIRCULACIÓN: PRÉSTAMOS Y DEVOLUCIONES ---");
            System.out.println("1. Registrar nuevo préstamo (RF-08)");
            System.out.println("2. Procesar devolución y cálculo de multas (RF-09)");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Opción: ");

            try {
                switch (opcion) {
                    case 1:
                        procesarNuevoPrestamo();
                        break;
                    case 2:
                        procesarDevolucionPrestamo();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("[Aviso] Opción inválida.");
                }
            } catch (BibliotecaException e) {
                System.out.println("\n[ERROR DE NEGOCIO] " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void procesarNuevoPrestamo() throws BibliotecaException {
        String folio = leerTexto("\nFolio del nuevo préstamo (ej. FOL-101): ");
        String idLector = leerTexto("ID del Lector solicitante: ");
        Usuario uLector = biblioteca.buscarUsuarioPorId(idLector);
        if (!(uLector instanceof Lector)) {
            throw new BibliotecaException("El ID '" + idLector + "' no pertenece a un Lector (Estudiante o Docente).");
        }

        String idBib = leerTexto("ID del Bibliotecario que autoriza: ");
        Usuario uBib = biblioteca.buscarUsuarioPorId(idBib);
        if (!(uBib instanceof Bibliotecario)) {
            throw new BibliotecaException("El ID '" + idBib + "' no corresponde a un Bibliotecario autorizado.");
        }

        Prestamo nuevoPrestamo = new Prestamo(folio, (Lector) uLector, (Bibliotecario) uBib);

        boolean agregarMas;
        do {
            String idMat = leerTexto("ID del material a prestar: ");
            MaterialBibliografico material = biblioteca.buscarMaterialPorId(idMat);
            int cantidad = leerEntero("Cantidad de ejemplares: ");

            nuevoPrestamo.agregarDetalle(material, cantidad);
            System.out.println("  -> Agregado: " + material.getTitulo() + " (x" + cantidad + ")");

            String resp = leerTexto("¿Desea agregar otro material al mismo folio? (S/N): ");
            agregarMas = resp.equalsIgnoreCase("S");
        } while (agregarMas);

        biblioteca.registrarPrestamo(nuevoPrestamo);
        System.out.println("\n[Éxito] Préstamo autorizado y registrado.");
        // GeneradorComprobante recibe el préstamo recién creado para emitir el ticket.
        System.out.println(comprobante.generarTicketPrestamo(nuevoPrestamo));
    }

    private static void procesarDevolucionPrestamo() throws BibliotecaException {
        String folio = leerTexto("\nFolio del préstamo a devolver: ");
        Prestamo prestamo = biblioteca.buscarPrestamoPorFolio(folio);
        System.out.println("Préstamo localizado: " + prestamo);

        System.out.println("Seleccione fecha de entrega:");
        System.out.println("1. Hoy (" + LocalDate.now() + ")");
        System.out.println("2. Ingresar fecha personalizada (AAAA-MM-DD, útil para simular retrasos)");
        int opcionFecha = leerEntero("Opción: ");

        LocalDate fechaEntrega = (opcionFecha == 2)
                ? leerFecha("Ingrese fecha de entrega (AAAA-MM-DD): ")
                : LocalDate.now();

        double multa = biblioteca.procesarDevolucion(folio, fechaEntrega);
        System.out.println("\n[Éxito] Devolución procesada correctamente.");
        System.out.println(comprobante.generarTicketDevolucion(prestamo, multa));
    }

    // submenú 4: Reportes y demostración polimórfica
    private static void menuReportes() throws EntidadNoEncontradaException {
        int opcion;
        do {
            System.out.println("\n--- REPORTES Y DEMOSTRACIÓN DE POO ---");
            System.out.println("1. Listar préstamos activos (RF-10)");
            System.out.println("2. Listar préstamos vencidos a la fecha actual (RF-10)");
            System.out.println("3. Reimprimir comprobante por folio (RF-10)");
            System.out.println("4. Demostración de Polimorfismo en ejecución");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Opción: ");

                switch (opcion) {
                    case 1:
                        List<Prestamo> activos = biblioteca.listarPrestamosActivos();
                        System.out.println("\n--- PRÉSTAMOS ACTIVOS (" + activos.size() + ") ---");
                        for (Prestamo p : activos) {
                            System.out.println(p);
                        }
                        break;
                    case 2:
                        List<Prestamo> vencidos = biblioteca.listarPrestamosVencidos(LocalDate.now());
                        System.out.println("\n--- PRÉSTAMOS VENCIDOS (" + vencidos.size() + ") ---");
                        for (Prestamo p : vencidos) {
                            System.out.println(p);
                        }
                        break;
                    case 3:
                        String folio = leerTexto("Ingrese folio del préstamo: ");
                        Prestamo p = biblioteca.buscarPrestamoPorFolio(folio);
                        if (p.isDevuelto()) {
                            System.out.println(comprobante.generarTicketDevolucion(p, p.calcularTotalMulta(p.getFechaDevolucionReal())));
                        } else {
                            System.out.println(comprobante.generarTicketPrestamo(p));
                        }
                        break;
                    case 4:
                        informacionGeneral();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("[Aviso] Opción inválida.");
                }
        } while (opcion != 0);
    }

    /**
     * Recorre colecciones con las superclases abstractas para evidenciar
     * cómo cada subclase ejecuta su propio comportamiento sobrescrito (@Override).
     */
    private static void informacionGeneral() {
        System.out.println("\n====================================================");
        System.out.println("  Información general del sistema");
        System.out.println("====================================================");
        System.out.println("1) Colección List<MaterialBibliografico> simulando 4 días de retraso:");
        for (MaterialBibliografico mat : biblioteca.getCatalogo()) {
            System.out.printf("   - %-42s | Clase: %-14s | Prestable: %-5b | Plazo: %d días | Multa (4d): $%.2f%n",
                    mat.getTitulo(),
                    mat.getClass().getSimpleName(),
                    mat.esPrestableExterno(),
                    mat.obtenerDiasPrestamo(),
                    mat.calcularMulta(4));
        }

        System.out.println("\n2) Colección List<Usuario> evaluando roles y privilegios:");
        for (Usuario usr : biblioteca.getUsuarios()) {
            if (usr instanceof Lector) {
                Lector lector = (Lector) usr;
                System.out.printf("   - %-20s | Rol: %-13s | Límite: %d ítems | Días extra: +%d%n",
                        lector.getNombre(),
                        lector.getRol(),
                        lector.getLimitePrestamos(),
                        lector.getDiasExtra());
            } else {
                System.out.printf("   - %-20s | Rol: %-13s | Privilegio: Operación y autorización administrativa%n",
                        usr.getNombre(),
                        usr.getRol());
            }
        }
        System.out.println("====================================================\n");
    }

    // Utilidades/métodos para ingreso de datos en consola
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[Entrada inválida] Por favor ingrese un número entero.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("[Formato inválido] Use el formato AAAA-MM-DD (ejemplo: 2026-11-25).");
            }
        }
    }
}