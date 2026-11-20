package Materiales;

/**
 * Representa un libro de circulación general disponible para préstamo a domicilio.
 */
// HERENCIA: Nivel 2 de la jerarquía (MaterialBibliografico -> Libro).
public class Libro extends MaterialBibliografico {

    public static final int DIAS_PRESTAMO_LIBRO = 7;
    public static final double TARIFA_MULTA_LIBRO = 10.0;

    protected String isbn;
    protected int numeroPaginas;

    /**
     * Constructor para registrar un libro en el catálogo.
     *
     * @param idMaterial            Identificador único en el sistema.
     * @param titulo                Título del libro.
     * @param autor                 Autor o autores del libro.
     * @param anioPublicacion       Año de edición.
     * @param ejemplaresDisponibles Número de ejemplares disponibles.
     * @param isbn                  Código estándar internacional ISBN.
     * @param numeroPaginas         Extensión total en páginas.
     */
    public Libro(String idMaterial, String titulo, String autor, int anioPublicacion,
                 int ejemplaresDisponibles, String isbn, int numeroPaginas) {
        super(idMaterial, titulo, autor, anioPublicacion, ejemplaresDisponibles);
        this.isbn = isbn;
        this.numeroPaginas = numeroPaginas;
    }

    // SOBREESCRITURA (@Override): Implementación específica de las reglas de préstamo y multa para libros.
    @Override
    public boolean esPrestableExterno() {
        return true;
    }

    @Override
    public int obtenerDiasPrestamo() {
        return DIAS_PRESTAMO_LIBRO;
    }

    @Override
    public double calcularMulta(int diasRetraso) {
        return diasRetraso > 0 ? diasRetraso * TARIFA_MULTA_LIBRO : 0.0;
    }

    @Override
    public String mostrarFicha() {
        String estado = activo ? "Activo" : "Inactivo";
        return String.format("[Libro] ID: %s | Título: %s | Autor: %s (%d) | ISBN: %s | Págs: %d | Disponibles: %d | Estado: %s",
                idMaterial, titulo, autor, anioPublicacion, isbn, numeroPaginas, ejemplaresDisponibles, estado);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }
}