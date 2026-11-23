package Servicios;

import Excepciones.BibliotecaException;
import Excepciones.EntidadNoEncontradaException;
import Excepciones.OperacionInvalidaBajaException;
import Excepciones.RegistroDuplicadoException;
import Excepciones.UsuarioNoElegibleException;
import Excepciones.BibliotecaException;

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
    public void registrarMaterial(MaterialBibliografico material) throws RegistroDuplicadoException, BibliotecaException {
        if (material == null) {
            throw new BibliotecaException("El material a registrar no puede ser nulo.");
        }
        if (material.getEjemplaresDisponibles() <= 0) {
            throw new BibliotecaException("El material debe registrarse con al menos 1 ejemplar disponible.");
        }
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.getIdMaterial().equalsIgnoreCase(material.getIdMaterial())) {
                throw new RegistroDuplicadoException("Material Bibliográfico", material.getIdMaterial());
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
    public MaterialBibliografico buscarMaterialPorId(String idMaterial) throws EntidadNoEncontradaException {
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.getIdMaterial().equalsIgnoreCase(idMaterial)) {
                return materialBibliografico;
            }
        }
        throw new EntidadNoEncontradaException("Material Bibliográfico", idMaterial);
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
    public void actualizarMaterial(String idMaterial, String nuevoTitulo, String nuevoAutor, int nuevosEjemplares)
            throws EntidadNoEncontradaException, BibliotecaException {
        MaterialBibliografico material = buscarMaterialPorId(idMaterial);
        if (nuevosEjemplares < 0) {
            throw new BibliotecaException("La cantidad de ejemplares disponibles no puede ser negativa.");
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
    public void darDeBajaMaterial(String idMaterial) throws EntidadNoEncontradaException, OperacionInvalidaBajaException {
        MaterialBibliografico material = buscarMaterialPorId(idMaterial);
        if (!material.isActivo()) {
            throw new OperacionInvalidaBajaException(String.format("El material '%s' ya se encuentra inactivo.", idMaterial));
        }

        // Valida RN-06: sin ejemplares prestados actualmente
        for (Prestamo prestamo : prestamos) {
            if (!prestamo.isDevuelto()) {
                for (DetallePrestamo d : prestamo.getDetalles()) {
                    if (d.getMaterial().getIdMaterial().equalsIgnoreCase(idMaterial)) {
                        throw new OperacionInvalidaBajaException(
                                String.format("No se puede dar de baja el material '%s' porque tiene ejemplares en el préstamo activo '%s' (RN-06).",
                                        idMaterial, prestamo.getFolio()));
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
    public void registrarUsuario(Usuario usuario) throws RegistroDuplicadoException, BibliotecaException {
        if (usuario == null) {
            throw new BibliotecaException("El usuario a registrar no puede ser nulo.");
        }
        for (Usuario u : usuarios) {
            if (u.getIdUsuario().equalsIgnoreCase(usuario.getIdUsuario())) {
                throw new RegistroDuplicadoException("Usuario", usuario.getIdUsuario());
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
    public Usuario buscarUsuarioPorId(String idUsuario) throws EntidadNoEncontradaException {
        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario().equalsIgnoreCase(idUsuario)) {
                return usuario;
            }
        }
        throw new EntidadNoEncontradaException("Usuario", idUsuario);
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
    public void actualizarCorreoUsuario(String idUsuario, String nuevoCorreo)
            throws EntidadNoEncontradaException, BibliotecaException {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (nuevoCorreo == null || nuevoCorreo.trim().isEmpty()) {
            throw new BibliotecaException("El correo electrónico no puede estar vacío.");
        }
        usuario.setCorreo(nuevoCorreo.trim());
    }

    /**
     * RF-06 (Actualización): Liquida el saldo total de multas de un lector.
     *
     * @param idLector Identificador del lector.
     */
    public void liquidarMultaLector(String idLector) throws EntidadNoEncontradaException, BibliotecaException {
        Usuario usuario = buscarUsuarioPorId(idLector);
        if (!(usuario instanceof Lector)) {
            throw new BibliotecaException("El usuario indicado no corresponde a un perfil de Lector.");
        }
        Lector lector = (Lector) usuario;
        if (!lector.tieneAdeudos()) {
            throw new BibliotecaException("El lector no tiene multas pendientes por liquidar.");
        }
        lector.liquidarAdeudo();
    }

    /**
     * RF-07 (Baja Lógica): Inhabilita la cuenta de un usuario verificando que no tenga
     * préstamos activos ni multas sin pagar (RN-06).
     *
     * @param idUsuario Identificador del usuario.
     */
    public void darDeBajaUsuario(String idUsuario) throws EntidadNoEncontradaException, OperacionInvalidaBajaException {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (!usuario.isActivo()) {
            throw new OperacionInvalidaBajaException(String.format("El usuario '%s' ya se encuentra inactivo.", idUsuario));
        }

        if (usuario instanceof Lector) {
            Lector lector = (Lector) usuario;
            if (lector.tieneAdeudos()) {
                throw new OperacionInvalidaBajaException(
                        String.format("No se puede dar de baja al lector '%s' porque mantiene un adeudo de $%.2f MXN (RN-06).",
                                idUsuario, lector.getAdeudoMultas()));
            }
            if (contarEjemplaresPrestadosALector(idUsuario) > 0) {
                throw new OperacionInvalidaBajaException(
                        String.format("No se puede dar de baja al lector '%s' porque tiene préstamos activos sin devolver (RN-06).",
                                idUsuario));
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
    public void registrarPrestamo(Prestamo prestamo)
            throws RegistroDuplicadoException, UsuarioNoElegibleException, BibliotecaException {
        if (prestamo == null) {
            throw new BibliotecaException("El préstamo no puede ser nulo.");
        }
        if (prestamo.getDetalles().isEmpty()) {
            throw new BibliotecaException("El préstamo debe contener al menos un material en su detalle.");
        }
        for (Prestamo p : prestamos) {
            if (p.getFolio().equalsIgnoreCase(prestamo.getFolio())) {
                throw new RegistroDuplicadoException("Préstamo", prestamo.getFolio());
            }
        }
        if (!prestamo.getBibliotecario().isActivo()) {
            throw new BibliotecaException("El bibliotecario que autoriza debe tener su cuenta activa.");
        }

        Lector lector = prestamo.getLector();

        // Valida RN-04: cuenta activa y sin adeudos
        if (!lector.isActivo()) {
            throw new UsuarioNoElegibleException(
                    String.format("La cuenta del lector '%s' está inactiva y no puede solicitar préstamos (RN-04).",
                            lector.getNombre()));
        }
        if (lector.tieneAdeudos()) {
            throw new UsuarioNoElegibleException(
                    String.format("El lector '%s' tiene un adeudo pendiente de $%.2f MXN. Debe liquidarlo antes de solicitar otro préstamo (RN-04).",
                            lector.getNombre(), lector.getAdeudoMultas()));
        }

        // Valida RN-04: sin préstamos vencidos actualmente
        for (Prestamo p : prestamos) {
            if (p.getLector().getIdUsuario().equalsIgnoreCase(lector.getIdUsuario()) && p.estaVencido(prestamo.getFechaPrestamo())) {
                throw new UsuarioNoElegibleException(
                        String.format("El lector '%s' tiene el préstamo vencido '%s'. Debe devolverlo primero (RN-04).",
                                lector.getNombre(), p.getFolio()));
            }
        }

        // Valida RN-02: límite polimórfico de materiales simultáneos (Estudiante: 3, Docente: 5)
        int actuales = contarEjemplaresPrestadosALector(lector.getIdUsuario());
        int nuevos = prestamo.obtenerTotalEjemplares();
        int limite = lector.getLimitePrestamos();

        if (actuales + nuevos > limite) {
            throw new UsuarioNoElegibleException(
                    String.format("'%s' excede su límite de %d materiales simultáneos (Actuales: %d, Solicitados: %d) (RN-02).",
                            lector.getNombre(), limite, actuales, nuevos));
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
    public double procesarDevolucion(String folio, LocalDate fechaEntrega)
            throws EntidadNoEncontradaException, BibliotecaException {
        Prestamo prestamo = buscarPrestamoPorFolio(folio);
        return prestamo.registrarDevolucion(fechaEntrega);
    }

    /**
     * RF-10 (Consulta): Busca un préstamo por su folio.
     *
     * @param folio Folio del préstamo.
     * @return El objeto Prestamo encontrado.
     */
    public Prestamo buscarPrestamoPorFolio(String folio) throws EntidadNoEncontradaException {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getFolio().equalsIgnoreCase(folio)) {
                return prestamo;
            }
        }
        throw new EntidadNoEncontradaException("Préstamo", folio);
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