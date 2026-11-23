package Excepciones;

/**
 * Se lanza al intentar aplicar una baja lógica (activo = false) sobre un material con ejemplares
 * actualmente prestados o sobre un usuario con préstamos activos o multas sin liquidar.
 */
// preserva la integridad referencial en bajas lógicas.
public class OperacionInvalidaBajaException extends BibliotecaException {

    /**
     * Construye la excepción explicando por qué no puede completarse la desactivación.
     * @param mensaje Detalle de las dependencias activas que impiden la baja lógica.
     */
    public OperacionInvalidaBajaException(String mensaje) {
        super(mensaje);
    }
}