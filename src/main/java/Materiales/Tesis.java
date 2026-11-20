package Materiales;

/**
 * Representa un trabajo de titulación o investigación académica resguardado en la biblioteca.
 */
// HERENCIA: Nivel 2 de la jerarquía (MaterialBibliografico -> Tesis).
public class Tesis extends MaterialBibliografico {

    public static final int DIAS_PRESTAMO_TESIS = 5;
    public static final double TARIFA_MULTA_TESIS = 15.0;

    protected GradoAcademico gradoAcademico;
    protected String institucion;

    /**
     * Constructor para registrar una tesis académica.
     *
     * @param idMaterial            Identificador único en el sistema.
     * @param titulo                Título de la investigación.
     * @param autor                 Autor o sustentante de la tesis.
     * @param anioPublicacion       Año de presentación.
     * @param ejemplaresDisponibles Número de ejemplares disponibles.
     * @param gradoAcademico        Nivel obtenido mediante el enum GradoAcademico.
     * @param institucion           Universidad o facultad de procedencia.
     */
    public Tesis(String idMaterial, String titulo, String autor, int anioPublicacion,
                 int ejemplaresDisponibles, GradoAcademico gradoAcademico, String institucion) {
        super(idMaterial, titulo, autor, anioPublicacion, ejemplaresDisponibles);
        this.gradoAcademico = gradoAcademico;
        this.institucion = institucion;
    }

    @Override
    public boolean esPrestableExterno() {
        return true;
    }

    @Override
    public int obtenerDiasPrestamo() {
        return DIAS_PRESTAMO_TESIS;
    }

    @Override
    public double calcularMulta(int diasRetraso) {
        return diasRetraso > 0 ? diasRetraso * TARIFA_MULTA_TESIS : 0.0;
    }

    @Override
    public String mostrarFicha() {
        String estado = activo ? "Activo" : "Inactivo";
        return String.format("[Tesis] ID: %s | Título: %s | Autor: %s (%d) | Grado: %s | Institución: %s | Disponibles: %d | Estado: %s",
                idMaterial, titulo, autor, anioPublicacion, gradoAcademico, institucion, ejemplaresDisponibles, estado);
    }

    public GradoAcademico getGradoAcademico() {
        return gradoAcademico;
    }

    public void setGradoAcademico(GradoAcademico gradoAcademico) {
        this.gradoAcademico = gradoAcademico;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }
}