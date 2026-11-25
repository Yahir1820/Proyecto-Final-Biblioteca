package Excepciones;

/**
 * Se lanza al intentar dar de alta un material, usuario o folio de préstamo
 * cuyo identificador único ya existe en el catálogo o padrón.
 */
// garantiza unicidad de claves en operaciones de alta (CRUD).
public class RegistroDuplicadoException extends BibliotecaException {

    /**
     * Construye la excepción indicando la entidad duplicada y su clave.
     * @param tipoEntidad Nombre de la entidad (ej. "Material Bibliográfico", "Usuario", "Préstamo").
     * @param identificador Identificador único repetido.
     */
    public RegistroDuplicadoException(String tipoEntidad, String identificador) {
        super(String.format("Ya existe un registro de %s con el identificador: '%s'.",
                tipoEntidad, identificador));
    }

    /**
     * Construye la excepción con un mensaje personalizado.
     * @param mensaje Mensaje descriptivo del error.
     */
    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}