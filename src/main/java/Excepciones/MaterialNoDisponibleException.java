package Excepciones;

/**
 * Se lanza cuando un material no cumple las condiciones para salir en préstamo externo, ya sea porque:
 * está dado de baja lógica, no tiene ejemplares disponibles o es un LibroConsulta de uso exclusivo en sala.
 */
// protege las reglas de circulación del acervo.
public class MaterialNoDisponibleException extends BibliotecaException {

    /**
     * Construye la excepción con el motivo específico por el cual el material no puede prestarse.
     * @param mensaje Detalle de la restricción (sin stock, inactivo o solo consulta en sala).
     */
    public MaterialNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
