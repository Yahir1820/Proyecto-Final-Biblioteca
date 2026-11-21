package Servicios;

import Usuarios.Bibliotecario;
import Usuarios.Lector;

import Materiales.MaterialBibliografico;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prestamo {
    public static final int MAX_ITEMS_POR_FOLIO = 5;

    protected String folio;
    protected LocalDate fechaPrestamo;
    protected LocalDate fechaDevolucionEsperada;
    protected LocalDate fechaDevolucionReal;
    protected boolean devuelto;

    protected Lector lector;
    protected Bibliotecario bibliotecario;

    protected List<DetallePrestamo> detalles;

    public Prestamo(String folio, Lector lector, Bibliotecario bibliotecario) {
        this(folio, lector, bibliotecario, LocalDate.now());
    }

    public Prestamo(String folio, Lector lector, Bibliotecario bibliotecario, LocalDate fechaPrestamo) {
        this.folio = folio;
        this.lector = lector;
        this.bibliotecario = bibliotecario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaPrestamo;
        this.devuelto = false;
        this.detalles = new ArrayList<>();
    }

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

        DetallePrestamo detalle = new DetallePrestamo(material, cantidad);
        this.detalles.add(detalle);

        int diasTotales = material.obtenerDiasPrestamo() + lector.getDiasExtra();
        LocalDate fechaCalculada = this.fechaPrestamo.plusDays(diasTotales);
        if (this.detalles.size() == 1 || fechaCalculada.isAfter(this.fechaDevolucionEsperada)) {
            this.fechaDevolucionEsperada = fechaCalculada;
        }
    }

    public int obtenerTotalEjemplares() {
        int total = 0;
        for (DetallePrestamo d : detalles) {
            total += d.getCantidad();
        }
        return total;
    }

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