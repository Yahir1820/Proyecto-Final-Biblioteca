package Materiales;

import Excepciones.BibliotecaException;

/**
 * Especialización de libro destinada exclusivamente a consulta interna dentro de las salas de lectura.
 */
// HERENCIA: Nivel 3 de la jerarquía (MaterialBibliografico -> Libro -> LibroConsulta).
public class LibroConsulta extends Libro {

    protected AreaReferencia areaReferencia;
    protected String ubicacionSala;

    /**
     * Constructor para registrar un libro de referencia o consulta en sala.
     *
     * @param idMaterial            Identificador único en el sistema.
     * @param titulo                Título de la obra de consulta.
     * @param autor                 Autor o institución editorial.
     * @param anioPublicacion       Año de publicación.
     * @param ejemplaresDisponibles Número de ejemplares en sala.
     * @param isbn                  Código ISBN.
     * @param numeroPaginas         Total de páginas.
     * @param areaReferencia        Categoría de referencia mediante el enum AreaReferencia.
     * @param ubicacionSala         Estantería o sala física asignada.
     */
    public LibroConsulta(String idMaterial, String titulo, String autor, int anioPublicacion,
                         int ejemplaresDisponibles, String isbn, int numeroPaginas,
                         AreaReferencia areaReferencia, String ubicacionSala) throws BibliotecaException {
        super(idMaterial, titulo, autor, anioPublicacion, ejemplaresDisponibles, isbn, numeroPaginas);
        setAreaReferencia(areaReferencia);
        this.ubicacionSala = ubicacionSala;
    }

    // SOBREESCRITURA (@Override): Restringe la salida a domicilio conforme a la regla de negocio RN-01.
    @Override
    public boolean esPrestableExterno() {
        return false;
    }

    @Override
    public int obtenerDiasPrestamo() {
        return 0;
    }

    @Override
    public double calcularMulta(int diasRetraso) {
        return 0.0;
    }

    @Override
    public String mostrarFicha() {
        String estado = activo ? "Activo" : "Inactivo";
        return String.format("[Libro Consulta - Solo Sala] ID: %s | Título: %s | Autor: %s (%d) | ISBN: %s | Área: %s | Ubicación: %s | Disponibles: %d | Estado: %s",
                idMaterial, titulo, autor, anioPublicacion, isbn, areaReferencia, ubicacionSala, ejemplaresDisponibles, estado);
    }

    public AreaReferencia getAreaReferencia() {
        return areaReferencia;
    }

    public void setAreaReferencia(AreaReferencia areaReferencia) throws BibliotecaException {
        if (areaReferencia == null) {
            throw new BibliotecaException("El área de referencia no puede ser nula.");
        }
        this.areaReferencia = areaReferencia;
    }

    public String getUbicacionSala() {
        return ubicacionSala;
    }

    public void setUbicacionSala(String ubicacionSala) {
        this.ubicacionSala = ubicacionSala;
    }
}