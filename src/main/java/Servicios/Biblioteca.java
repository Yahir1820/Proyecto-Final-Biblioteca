package Servicios;

import Materiales.MaterialBibliografico;
import Usuarios.Lector;
import Usuarios.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Biblioteca {
    protected String nombre;

    protected List<MaterialBibliografico> catalogo;
    protected List<Usuario> usuarios;

    protected List<Prestamo> prestamos;

    public Biblioteca(String nombre) {
        this.nombre = nombre;
        this.catalogo = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.prestamos = new ArrayList<>();
    }

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

    public MaterialBibliografico buscarMaterialPorId(String idMaterial) {
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.getIdMaterial().equalsIgnoreCase(idMaterial)) {
                return materialBibliografico;
            }
        }
        System.out.println("Material no encontrado " + idMaterial);
        return null;
    }

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

    public List<MaterialBibliografico> listarMaterialesActivos() {
        List<MaterialBibliografico> activos = new ArrayList<>();
        for (MaterialBibliografico materialBibliografico : catalogo) {
            if (materialBibliografico.isActivo()) {
                activos.add(materialBibliografico);
            }
        }
        return activos;
    }

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

    public void darDeBajaMaterial(String idMaterial) {
        MaterialBibliografico material = buscarMaterialPorId(idMaterial);
        if (!material.isActivo()) {
            System.out.printf("El material '%s' ya se encuentra inactivo.\n", idMaterial);
            return;
        }

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

    public Usuario buscarUsuarioPorId(String idUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario().equalsIgnoreCase(idUsuario)) {
                return usuario;
            }
        }
        System.out.println("Usuario no encontrado. Usuario " + idUsuario);
        return null;
    }

    public List<Usuario> listarUsuariosActivos() {
        List<Usuario> activos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.isActivo()) {
                activos.add(usuario);
            }
        }
        return activos;
    }

    public void actualizarCorreoUsuario(String idUsuario, String nuevoCorreo) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (nuevoCorreo == null || nuevoCorreo.trim().isEmpty()) {
            System.out.println("El correo electrónico no puede estar vacío.");
            return;
        }
        usuario.setCorreo(nuevoCorreo.trim());
    }

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

        if (!lector.isActivo()) {
            System.out.printf("La cuenta del lector '%s' está inactiva y no puede solicitar préstamos.\n", lector.getNombre());
            return;
        }
        if (lector.tieneAdeudos()) {
            System.out.printf("El lector '%s' tiene un adeudo pendiente de $%.2f MXN. Debe liquidarlo antes de solicitar otro préstamo.\n",
                    lector.getNombre(), lector.getAdeudoMultas());
            return;
        }

        for (Prestamo p : prestamos) {
            if (p.getLector().getIdUsuario().equalsIgnoreCase(lector.getIdUsuario())
                    && p.estaVencido(prestamo.getFechaPrestamo())) {
                System.out.printf("El lector '%s' tiene el préstamo vencido '%s'. Debe devolverlo primero.\n",
                        lector.getNombre(), p.getFolio());
                return;
            }
        }

        int actuales = contarEjemplaresPrestadosALector(lector.getIdUsuario());
        int nuevos = prestamo.obtenerTotalEjemplares();
        int limite = lector.getLimitePrestamos();

        if (actuales + nuevos > limite) {
            System.out.printf("'%s' excede su límite de %d materiales simultáneos (actuales: %d, solicitados: %d)\n",
                    lector.getNombre(), limite, actuales, nuevos);
            return;
        }

        for (DetallePrestamo detalle : prestamo.getDetalles()) {
            for (int i = 0; i < detalle.getCantidad(); i++) {
                detalle.getMaterial().reducirStock();
            }
        }
        prestamos.add(prestamo);
    }

    public double procesarDevolucion(String folio, LocalDate fechaEntrega) {
        Prestamo prestamo = buscarPrestamoPorFolio(folio);
        return prestamo.registrarDevolucion(fechaEntrega);
    }

    public Prestamo buscarPrestamoPorFolio(String folio) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getFolio().equalsIgnoreCase(folio)) {
                return prestamo;
            }
        }
        System.out.println("Préstamo no encontrado. Préstamo " + folio);
        return null;
    }

    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (!prestamo.isDevuelto()) {
                activos.add(prestamo);
            }
        }
        return activos;
    }

    public List<Prestamo> listarPrestamosVencidos(LocalDate fechaReferencia) {
        List<Prestamo> vencidos = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaVencido(fechaReferencia)) {
                vencidos.add(prestamo);
            }
        }
        return vencidos;
    }

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