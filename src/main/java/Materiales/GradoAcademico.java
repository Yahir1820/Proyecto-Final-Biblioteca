package Materiales;

/**
 * Enumeración que clasifica el nivel académico de una tesis de investigación.
 */
// ENUM: Estandariza los grados académicos válidos para el catálogo de tesis.
public enum GradoAcademico {
    LICENCIATURA("Licenciatura"),
    ESPECIALIDAD("Especialidad"),
    MAESTRIA("Maestría"),
    DOCTORADO("Doctorado");

    private final String etiqueta;

    GradoAcademico(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}