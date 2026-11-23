package Servicios;

import Materiales.MaterialBibliografico;
import Usuarios.Lector;
import Usuarios.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase gestora principal del sistema bibliotecario.
 * Coordina las operaciones CRUD sobre materiales, usuarios y préstamos,
 * haciendo cumplir todas las reglas de negocio del dominio.
 */
public class Biblioteca {
    protected String nombre;

    // AGREGACIÓN: La biblioteca agrupa materiales y usuarios, pero estas entidades pueden
    // existir de manera independiente fuera del ciclo de vida de esta instancia.
    protected List<MaterialBibliografico> catalogo;
    protected List<Usuario> usuarios;

    // COMPOSICIÓN: La biblioteca posee y controla el historial transaccional de préstamos.
    protected List<Prestamo> prestamos;

    /**
     * Inicializa la biblioteca con sus colecciones vacías.
     *
     * @param nombre Nombre institucional de la biblioteca.
     */
    public Biblioteca(String nombre) {
        this.nombre = nombre;
        this.catalogo = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.prestamos = new ArrayList<>();
    }

    // CRUD del material bibliográfico (del RF-01 al RF-04)

    /**
     * RF-01 (Alta): Registra un nuevo material en el catálogo verificando que su ID no exista.
     *
     * @param material Subclase concreta de MaterialBibliografico.
     */
    public void registrarMaterial(MaterialBibliografico material) {
        if (material == null) {
            System.out.println("El material a registrar no puede ser nulo.");
            return;
        }
        if (material.getEjemplaresDisponibles() <= 0) {
            System.out.println("El material debe registrarse con al menos 1 ejemplar disponible.");
            return;
        }
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.getIdMaterial().equalsIgnoreCase(material.getIdMaterial())) {
                System.out.println("Registro duplicado. Material Bibliográfico " + material.getIdMaterial());
                return;
            }
        }
        catalogo.add(material);
    }

    /**
     * RF-02 (Consulta): Busca un material por su identificador único.
     *
     * @param idMaterial Código del material.
     * @return El objeto MaterialBibliografico localizado.
     */
    public MaterialBibliografico buscarMaterialPorId(String idMaterial) {
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.getIdMaterial().equalsIgnoreCase(idMaterial)) {
                return materialBibliografico;
            }
        }
        System.out.println("Material no encontrado " + idMaterial);
        return null;
    }

    /**
     * RF-02 (Consulta): Filtra materiales activos por coincidencia parcial en título o autor.
     *
     * @param criterio Texto a buscar.
     * @return Lista de materiales activos que coinciden con el criterio.
     */
    public List<MaterialBibliografico> buscarMaterialesPorTexto(String criterio) {
        List<MaterialBibliografico> resultados = new ArrayList<>();
        String filtro = criterio.toLowerCase().trim();
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.isActivo() && (materialBibliografico.getTitulo().toLowerCase().contains(filtro)
                    || materialBibliografico.getAutor().toLowerCase().contains(filtro))) {
                resultados.add(materialBibliografico);
            }
        }
        return resultados;
    }

    /**
     * RF-02 (Consulta): Devuelve todos los materiales actualmente activos en el catálogo.
     *
     * @return Lista de materiales activos.
     */
    public List<MaterialBibliografico> listarMaterialesActivos() {
        List<MaterialBibliografico> activos = new ArrayList<>();
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.isActivo()) {
                activos.add(materialBibliografico);
            }
        }
        return activos;
    }

    /**
     * RF-03 (Actualización): Modifica título, autor y existencias disponibles de un material.
     *
     * @param idMaterial       ID del material a actualizar.
     * @param nuevoTitulo      Nuevo título.
     * @param nuevoAutor       Nuevo autor.
     * @param nuevosEjemplares Nueva cantidad de ejemplares disponibles.
     */
    public void actualizarMaterial(String idMaterial, String nuevoTitulo, String nuevoAutor, int nuevosEjemplares) {
        MaterialBibliografico material = buscarMaterialPorId(idMaterial);
        if (nuevosEjemplares < 0) {
            System.out.println("La cantidad de ejemplares disponibles no puede ser negativa.");
            return;
        }
        if (nuevoTitulo != null && !nuevoTitulo.trim().isEmpty()) {
            material.setTitulo(nuevoTitulo.trim());
        }
        if (nuevoAutor != null && !nuevoAutor.trim().isEmpty()) {
            material.setAutor(nuevoAutor.trim());
        }
        material.setEjemplaresDisponibles(nuevosEjemplares);
    }

    /**
     * RF-04 (Baja Lógica): Desactiva un material (activo = false) verificando antes
     * que ningún ejemplar se encuentre actualmente en un préstamo sin devolver (RF-06).
     *
     * @param idMaterial ID del material a dar de baja.
     */
    public void darDeBajaMaterial(String idMaterial) {
        MaterialBibliografico material = buscarMaterialPorId(idMaterial);
        if (!material.isActivo()) {
            System.out.printf("El material '%s' ya se encuentra inactivo.\n", idMaterial);
            return;
        }

        // Valida RN-06: sin ejemplares prestados actualmente
        for (Prestamo prestamo : prestamos) {
            if (!prestamo.isDevuelto()) {
                for (DetallePrestamo d : prestamo.getDetalles()) {
                    if (d.getMaterial().getIdMaterial().equalsIgnoreCase(idMaterial)) {
                        System.out.printf("No se puede dar de baja el material '%s' porque tiene ejemplares en el préstamo activo '%s'.\n",
                                idMaterial, prestamo.getFolio());
                        return;
                    }
                }
            }
        }
        material.setActivo(false);
    }

    // CRUD de los usuarios (del RF-05 al RF-07)

    /**
     * RF-05 (Alta): Registra un nuevo usuario (Estudiante, Docente o Bibliotecario).
     *
     * @param usuario Instancia concreta de Usuario.
     */
    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            System.out.println("El usuario a registrar no puede ser nulo.");
            return;
        }
        for (Usuario u : usuarios) {
            if (u.getIdUsuario().equalsIgnoreCase(usuario.getIdUsuario())) {
                System.out.println("Usuario duplicado. Usuario " + usuario.getIdUsuario());
                return;
            }
        }
        usuarios.add(usuario);
    }

    /**
     * RF-06 (Consulta): Localiza un usuario por su ID.
     *
     * @param idUsuario Identificador del usuario.
     * @return El objeto Usuario correspondiente.
     */
    public Usuario buscarUsuarioPorId(String idUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario().equalsIgnoreCase(idUsuario)) {
                return usuario;
            }
        }
        System.out.println("Usuario no encontrado. Usuario " + idUsuario);
        return null;
    }

    /**
     * RF-06 (Consulta): Lista todos los usuarios activos.
     *
     * @return Lista de usuarios activos.
     */
    public List<Usuario> listarUsuariosActivos() {
        List<Usuario> activos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.isActivo()) {
                activos.add(usuario);
            }
        }
        return activos;
    }

    /**
     * RF-06 (Actualización): Actualiza el correo electrónico de un usuario registrado.
     *
     * @param idUsuario   Identificador del usuario.
     * @param nuevoCorreo Nuevo correo electrónico.
     */
    public void actualizarCorreoUsuario(String idUsuario, String nuevoCorreo) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (nuevoCorreo == null || nuevoCorreo.trim().isEmpty()) {
            System.out.println("El correo electrónico no puede estar vacío.");
            return;
        }
        usuario.setCorreo(nuevoCorreo.trim());
    }

    /**
     * RF-06 (Actualización): Liquida el saldo total de multas de un lector.
     *
     * @param idLector Identificador del lector.
     */
    public void liquidarMultaLector(String idLector) {
        Usuario usuario = buscarUsuarioPorId(idLector);
        if (!(usuario instanceof Lector)) {
            System.out.println("El usuario indicado no corresponde a un perfil de Lector.");
            return;
        }
        Lector lector = (Lector) usuario;
        if (!lector.tieneAdeudos()) {
            System.out.println("El lector no tiene multas pendientes por liquidar.");
            return;
        }
        lector.liquidarAdeudo();
    }

    /**
     * RF-07 (Baja Lógica): Inhabilita la cuenta de un usuario verificando que no tenga
     * préstamos activos ni multas sin pagar (RN-06).
     *
     * @param idUsuario Identificador del usuario.
     */
    public void darDeBajaUsuario(String idUsuario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (!usuario.isActivo()) {
            System.out.printf("El usuario '%s' ya se encuentra inactivo.\n", idUsuario);
            return;
        }

        if (usuario instanceof Lector) {
            Lector lector = (Lector) usuario;
            if (lector.tieneAdeudos()) {
                System.out.printf("No se puede dar de baja al lector '%s' porque mantiene un adeudo de $%.2f MXN.\n",
                        idUsuario, lector.getAdeudoMultas());
                return;
            }
            if (contarEjemplaresPrestadosALector(idUsuario) > 0) {
                System.out.printf("No se puede dar de baja al lector '%s' porque tiene préstamos activos sin devolver.\n", idUsuario);
                return;
            }
        }
        usuario.desactivar();
    }

    // CIRCULACIÓN: préstamos, devoluciones y reportes (RF-08 a RF-10)

    /**
     * RF-08 (Alta de Préstamo): Valida el folio, la elegibilidad del lector (RN-04),
     * su límite polimórfico de préstamos simultáneos (RN-02) y descuenta el inventario.
     *
     * @param prestamo Objeto Prestamo previamente armado con sus detalles.
     */
    public void registrarPrestamo(Prestamo prestamo) {
        if (prestamo == null) {
            System.out.println("El préstamo no puede ser nulo.");
            return;
        }
        if (prestamo.getDetalles().isEmpty()) {
            System.out.println("El préstamo debe contener al menos un material en su detalle.");
            return;
        }
        for (Prestamo p : prestamos) {
            if (p.getFolio().equalsIgnoreCase(prestamo.getFolio())) {
                System.out.println("Préstamo duplicado. Préstamo " + prestamo.getFolio());
                return;
            }
        }
        if (!prestamo.getBibliotecario().isActivo()) {
            System.out.println("El bibliotecario que autoriza debe tener su cuenta activa.");
            return;
        }

        Lector lector = prestamo.getLector();

        // Valida RN-04: cuenta activa y sin adeudos
        if (!lector.isActivo()) {
            System.out.printf("La cuenta del lector '%s' está inactiva y no puede solicitar préstamos.\n", lector.getNombre());
            return;
        }
        if (lector.tieneAdeudos()) {
            System.out.printf("El lector '%s' tiene un adeudo pendiente de $%.2f MXN. Debe liquidarlo antes de solicitar otro préstamo.\n",
                    lector.getNombre(), lector.getAdeudoMultas());
            return;
        }

        // Valida RN-04: sin préstamos vencidos actualmente
        for (Prestamo p : prestamos) {
            if (p.getLector().getIdUsuario().equalsIgnoreCase(lector.getIdUsuario())
                    && p.estaVencido(prestamo.getFechaPrestamo())) {
                System.out.printf("El lector '%s' tiene el préstamo vencido '%s'. Debe devolverlo primero.\n",
                        lector.getNombre(), p.getFolio());
                return;
            }
        }

        // Valida RN-02: límite polimórfico de materiales simultáneos (Estudiante: 3, Docente: 5)
        int actuales = contarEjemplaresPrestadosALector(lector.getIdUsuario());
        int nuevos = prestamo.obtenerTotalEjemplares();
        int limite = lector.getLimitePrestamos();

        if (actuales + nuevos > limite) {
            System.out.printf("'%s' excede su límite de %d materiales simultáneos (actuales: %d, solicitados: %d)\n",
                    lector.getNombre(), limite, actuales, nuevos);
            return;
        }

        // Descuenta ejemplares del inventario disponible y guarda el préstamo
        for (DetallePrestamo detalle : prestamo.getDetalles()) {
            for (int i = 0; i < detalle.getCantidad(); i++) {
                detalle.getMaterial().reducirStock();
            }
        }
        prestamos.add(prestamo);
    }

    /**
     * RF-09 (Devolución y Cálculo de Multas): Procesa la entrega de un préstamo por su folio.
     *
     * @param folio Folio del préstamo a devolver.
     * @param fechaEntrega Fecha real en que se entregan los materiales.
     * @return Monto de multa generada (0.0 si no hubo retraso).
     */
    public double procesarDevolucion(String folio, LocalDate fechaEntrega) {
        Prestamo prestamo = buscarPrestamoPorFolio(folio);
        return prestamo.registrarDevolucion(fechaEntrega);
    }

    /**
     * RF-10 (Consulta): Busca un préstamo por su folio.
     *
     * @param folio Folio del préstamo.
     * @return El objeto Prestamo encontrado.
     */
    public Prestamo buscarPrestamoPorFolio(String folio) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getFolio().equalsIgnoreCase(folio)) {
                return prestamo;
            }
        }
        System.out.println("Préstamo no encontrado. Préstamo " + folio);
        return null;
    }

    /**
     * RF-10 (Consulta): Obtiene todos los préstamos que aún no han sido devueltos.
     *
     * @return Lista de préstamos vigentes.
     */
    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (!prestamo.isDevuelto()) {
                activos.add(prestamo);
            }
        }
        return activos;
    }

    /**
     * RF-10 (Consulta): Obtiene todos los préstamos activos cuya fecha límite ya venció.
     *
     * @param fechaReferencia Fecha actual (o de corte).
     * @return Lista de préstamos vencidos.
     */
    public List<Prestamo> listarPrestamosVencidos(LocalDate fechaReferencia) {
        List<Prestamo> vencidos = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaVencido(fechaReferencia)) {
                vencidos.add(prestamo);
            }
        }
        return vencidos;
    }

    /**
     * Calcula cuántos ejemplares tiene actualmente en préstamo (sin devolver) un lector.
     *
     * @param idLector Identificador del lector.
     * @return Total de ejemplares activos en posesión del lector.
     */
    public int contarEjemplaresPrestadosALector(String idLector) {
        int total = 0;
        for (Prestamo prestamo : prestamos) {
            if (!prestamo.isDevuelto() && prestamo.getLector().getIdUsuario().equalsIgnoreCase(idLector)) {
                total += prestamo.obtenerTotalEjemplares();
            }
        }
        return total;
    }

    public String getNombre() {
        return nombre;
    }

    public List<MaterialBibliografico> getCatalogo() {
        return Collections.unmodifiableList(catalogo);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public List<Prestamo> getPrestamos() {
        return Collections.unmodifiableList(prestamos);
    }
}