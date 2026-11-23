package Servicios;

import Usuarios.Bibliotecario;
import Usuarios.Lector;

import Materiales.MaterialBibliografico;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad transaccional que modela el préstamo de uno o varios materiales bibliográficos
 * a un lector, y es autorizado por un bibliotecario.
 */
public class Prestamo {
    public static final int MAX_ITEMS_POR_FOLIO = 5;

    protected String folio;
    protected LocalDate fechaPrestamo;
    protected LocalDate fechaDevolucionEsperada;
    protected LocalDate fechaDevolucionReal;
    protected boolean devuelto;

    // ASOCIACIÓN: Vincula la transacción con el lector solicitante y el bibliotecario que autoriza.
    protected Lector lector;
    protected Bibliotecario bibliotecario;

    // COMPOSICIÓN: El préstamo crea y controla el ciclo de vida de sus líneas de detalle (1..5).
    protected List<DetallePrestamo> detalles;

    /**
     * Crea un nuevo préstamo con fecha actual.
     *
     * @param folio Folio único de la transacción.
     * @param lector Lector que solicita el préstamo.
     * @param bibliotecario Personal que autoriza la salida del material.
     */
    public Prestamo(String folio, Lector lector, Bibliotecario bibliotecario) {
        this(folio, lector, bibliotecario, LocalDate.now());
    }

    /**
     * Crea un nuevo préstamo especificando la fecha de inicio (útil para pruebas de vencimiento).
     *
     * @param folio Folio único de la transacción.
     * @param lector Lector que solicita el préstamo.
     * @param bibliotecario Personal que autoriza la salida del material.
     * @param fechaPrestamo Fecha en la que se realiza el préstamo.
     */
    public Prestamo(String folio, Lector lector, Bibliotecario bibliotecario, LocalDate fechaPrestamo) {
        this.folio = folio;
        this.lector = lector;
        this.bibliotecario = bibliotecario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaPrestamo;
        this.devuelto = false;
        // COMPOSICIÓN: La lista de detalles nace exclusivamente dentro del constructor de Prestamo.
        this.detalles = new ArrayList<>();
    }

    /**
     * Agrega una línea de detalle al préstamo validando la disponibilidad y elegibilidad
     * del material (RN-01), así como el cálculo polimórfico del plazo de entrega (RN-03).
     *
     * @param material Material bibliográfico a prestar.
     * @param cantidad Número de ejemplares solicitados.
     */
    public void agregarDetalle(MaterialBibliografico material, int cantidad) {
        if (material == null) {
            System.out.println("El material bibliográfico no puede ser nulo.");
            return;
        }
        if (cantidad <= 0) {
            System.out.println("La cantidad de ejemplares a prestar debe ser mayor a cero.");
            return;
        }
        if (!material.isActivo()) {
            System.out.printf("El material '%s' (%s) se encuentra dado de baja lógica.\n",
                    material.getTitulo(), material.getIdMaterial());
            return;
        }

        // POLIMORFISMO: Verifica si el tipo de material permite préstamo externo (LibroConsulta retorna false).
        if (!material.esPrestableExterno()) {
            System.out.printf("El material '%s' (%s) es exclusivo de consulta en sala y no puede salir a domicilio.\n",
                    material.getTitulo(), material.getIdMaterial());
            return;
        }
        if (material.getEjemplaresDisponibles() < cantidad) {
            System.out.printf("Stock insuficiente para '%s'. Disponibles: %d, Solicitados: %d.\n",
                    material.getTitulo(), material.getEjemplaresDisponibles(), cantidad);
            return;
        }
        if (obtenerTotalEjemplares() + cantidad > MAX_ITEMS_POR_FOLIO) {
            System.out.printf("Un mismo folio de préstamo no puede exceder %d ejemplares en total.\n", MAX_ITEMS_POR_FOLIO);
            return;
        }

        // COMPOSICIÓN: Instancia el DetallePrestamo dentro del ciclo de vida del Prestamo.
        DetallePrestamo detalle = new DetallePrestamo(material, cantidad);
        this.detalles.add(detalle);

        // POLIMORFISMO (RN-03): Plazo base del material + días extra del perfil del lector (Estudiante vs Docente).
        int diasTotales = material.obtenerDiasPrestamo() + lector.getDiasExtra();
        LocalDate fechaCalculada = this.fechaPrestamo.plusDays(diasTotales);
        if (this.detalles.size() == 1 || fechaCalculada.isAfter(this.fechaDevolucionEsperada)) {
            this.fechaDevolucionEsperada = fechaCalculada;
        }
    }

    /**
     * Calcula la suma total de ejemplares incluidos en este préstamo.
     *
     * @return Cantidad total de piezas en todos los detalles.
     */
    public int obtenerTotalEjemplares() {
        int total = 0;
        for (DetallePrestamo d : detalles) {
            total += d.getCantidad();
        }
        return total;
    }

    /**
     * Calcula el monto total de multa acumulada según la fecha de entrega indicada (RN-05).
     *
     * @param fechaEntrega Fecha real o proyectada de devolución.
     * @return Suma de las multas calculadas polimórficamente por cada detalle.
     */
    public double calcularTotalMulta(LocalDate fechaEntrega) {
        long diasRetraso = ChronoUnit.DAYS.between(this.fechaDevolucionEsperada, fechaEntrega);
        if (diasRetraso <= 0) {
            return 0.0;
        }
        double totalMulta = 0.0;
        for (DetallePrestamo detalle : detalles) {
            totalMulta += detalle.calcularMultaItem((int) diasRetraso);
        }
        return totalMulta;
    }

    /**
     * Registra la devolución definitiva del préstamo, restaura el stock de cada material
     * y carga cualquier multa generada al saldo del lector.
     *
     * @param fechaEntrega Fecha en que el lector entrega los materiales.
     * @return Monto total de la multa generada (0.0 si se entregó a tiempo).
     */
    public double registrarDevolucion(LocalDate fechaEntrega) {
        if (this.devuelto) {
            System.out.printf("El préstamo con folio '%s' ya fue devuelto anteriormente.\n", this.folio);
            return 0.0;
        }
        if (fechaEntrega == null) {
            fechaEntrega = LocalDate.now();
        }

        this.fechaDevolucionReal = fechaEntrega;
        double multaGenerada = calcularTotalMulta(fechaEntrega);

        // Restaurar inventario disponible de cada material prestado
        for (DetallePrestamo detalle : detalles) {
            for (int i = 0; i < detalle.getCantidad(); i++) {
                detalle.getMaterial().incrementarStock();
            }
        }

        if (multaGenerada > 0) {
            this.lector.agregarAdeudoMultas(multaGenerada);
        }

        this.devuelto = true;
        return multaGenerada;
    }

    /**
     * Indica si el préstamo aún no se devuelve y ya superó su fecha límite.
     *
     * @param fechaReferencia Fecha contra la cual se evalúa el vencimiento.
     * @return true si está pendiente y vencido.
     */
    public boolean estaVencido(LocalDate fechaReferencia) {
        return !this.devuelto && fechaReferencia.isAfter(this.fechaDevolucionEsperada);
    }

    public String getFolio() {
        return folio;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public Lector getLector() {
        return lector;
    }

    public Bibliotecario getBibliotecario() {
        return bibliotecario;
    }

    public List<DetallePrestamo> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    @Override
    public String toString() {
        String estado = devuelto ? "Devuelto (" + fechaDevolucionReal + ")" : "Activo (Vence: " + fechaDevolucionEsperada + ")";
        return String.format("Folio: %s | Lector: %s | Fecha: %s | Estado: %s | Ítems: %d",
                folio, lector.getNombre(), fechaPrestamo, estado, obtenerTotalEjemplares());
    }
}