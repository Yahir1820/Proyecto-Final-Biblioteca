package Materiales;

/**
 * Contrato que define el comportamiento de circulación, plazos y sanciones de los materiales bibliográficos dentro del sistema.
 */
// INTERFAZ: Define métodos polimórficos que cada tipo de material implementará según sus reglas de negocio.
public interface Prestable {

    /**
     * Indica si el material puede salir en préstamo externo a domicilio.
     *
     * @return true si permite préstamo externo; false si es exclusivamente de consulta en sala.
     */
    boolean esPrestableExterno();

    /**
     * Obtiene el plazo base en días naturales para el préstamo del material.
     *
     * @return Número de días base de préstamo.
     */
    int obtenerDiasPrestamo();

    /**
     * Calcula el monto total de multa por días de retraso en la devolución (RN-05).
     *
     * @param diasRetraso Cantidad de días naturales transcurridos después de la fecha límite.
     * @return Monto monetario de la sanción en MXN.
     */
    double calcularMulta(int diasRetraso);
}