package Excepciones;

/**
 * Se lanza cuando una consulta, actualización, baja o transacción hace referencia
 * a un material, usuario o préstamo inexistente en el sistema.
 */
// Especializa a BibliotecaException para errores de búsqueda en colecciones.
public class EntidadNoEncontradaException extends BibliotecaException {

    /**
     * Construye la excepción indicando el tipo de entidad y el identificador buscado.
     * @param tipoEntidad nombre de la entidad por ejemplo "Material Bibliográfico", "Usuario", "Préstamo".
     * @param identificador ID o folio que no fue localizado.
     */
    public EntidadNoEncontradaException(String tipoEntidad, String identificador) {
        super(String.format("No se encontró ningún registro de %s con el identificador: '%s'.",
                tipoEntidad, identificador));
    }

    /**
     * Construye la excepción con un mensaje personalizado.
     * @param mensaje Mensaje descriptivo del error.
     */
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}