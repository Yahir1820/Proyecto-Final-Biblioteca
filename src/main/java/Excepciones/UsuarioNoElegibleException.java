package Excepciones;

/**
 * Se lanza cuando un lector intenta solicitar un préstamo sin cumplir las políticas vigentes:
 * cuenta inactiva, adeudos (por multas anteriores) pendientes, préstamos vencidos o límite simultáneo alcanzado.
 */
// valida los privilegios y restricciones del lector.
public class UsuarioNoElegibleException extends BibliotecaException {

    /**
     * Construye la excepción detallando el impedimento del lector para tramitar el préstamo.
     * @param mensaje Descripción del bloqueo o límite excedido.
     */
    public UsuarioNoElegibleException(String mensaje) {
        super(mensaje);
    }
}