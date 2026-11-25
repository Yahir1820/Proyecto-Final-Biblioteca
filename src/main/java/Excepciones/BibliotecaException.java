package Excepciones;

/**
 * Excepción base verificada (checked) para todas las reglas de negocio y validaciones
 * del sistema bibliotecario.
 */
// clase central de errores que obliga al manejo con try-catch.
public class BibliotecaException extends Exception {

    /**
     * Construye una nueva excepción de dominio con un mensaje descriptivo.
     * @param mensaje Descripción clara del error o regla de negocio infringida.
     */
    public BibliotecaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una nueva excepción de dominio conservando la causa original.
     * @param mensaje Descripción del error.
     * @param causa   Excepción subyacente que originó el fallo.
     */
    public BibliotecaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
