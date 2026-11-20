package Materiales;

/**
 * Representa una publicación periódica o revista científica del acervo.
 */
// HERENCIA: Nivel 2 de la jerarquía (MaterialBibliografico -> Revista).
public class Revista extends MaterialBibliografico {

    public static final int DIAS_PRESTAMO_REVISTA = 3;
    public static final double TARIFA_MULTA_REVISTA = 5.0;

    protected String issn;
    protected int numeroEdicion;

    /**
     * Constructor para registrar una revista en el catálogo.
     *
     * @param idMaterial            Identificador único en el sistema.
     * @param titulo                Nombre de la revista.
     * @param autor                 Editorial o comité responsable.
     * @param anioPublicacion       Año de publicación del fascículo.
     * @param ejemplaresDisponibles Número de ejemplares disponibles.
     * @param issn                  Código internacional ISSN.
     * @param numeroEdicion         Número o volumen de la edición.
     */
    public Revista(String idMaterial, String titulo, String autor, int anioPublicacion,
                   int ejemplaresDisponibles, String issn, int numeroEdicion) {
        super(idMaterial, titulo, autor, anioPublicacion, ejemplaresDisponibles);
        this.issn = issn;
        this.numeroEdicion = numeroEdicion;
    }

    @Override
    public boolean esPrestableExterno() {
        return true;
    }

    @Override
    public int obtenerDiasPrestamo() {
        return DIAS_PRESTAMO_REVISTA;
    }

    @Override
    public double calcularMulta(int diasRetraso) {
        return diasRetraso > 0 ? diasRetraso * TARIFA_MULTA_REVISTA : 0.0;
    }

    @Override
    public String mostrarFicha() {
        String estado = activo ? "Activo" : "Inactivo";
        return String.format("[Revista] ID: %s | Título: %s | Editorial: %s (%d) | ISSN: %s | Edición: #%d | Disponibles: %d | Estado: %s",
                idMaterial, titulo, autor, anioPublicacion, issn, numeroEdicion, ejemplaresDisponibles, estado);
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    public void setNumeroEdicion(int numeroEdicion) {
        this.numeroEdicion = numeroEdicion;
    }
}