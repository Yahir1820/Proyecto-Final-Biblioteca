package Materiales;

/**
 * Enumeración que categoriza las obras de consulta interna en sala.
 */
// ENUM: Define las categorías válidas para los libros exclusivos de consulta en sala.
public enum AreaReferencia {
    ENCICLOPEDIA("Enciclopedia"),
    DICCIONARIO("Diccionario"),
    ATLAS("Atlas"),
    NORMATIVA("Normativa"),
    MANUAL_TECNICO("Manual Técnico"),
    OTRA("Otra");

    private final String etiqueta;

    AreaReferencia(String etiqueta) {
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