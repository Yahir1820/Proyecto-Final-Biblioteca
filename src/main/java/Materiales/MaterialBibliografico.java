package Materiales;

/**
 * Clase abstracta base que concentra los atributos y operaciones comunes
 * del acervo documental de la biblioteca.
 */
// CLASE ABSTRACTA: Agrupa el estado compartido del acervo e implementa el contrato Prestable sin permitir instanciación directa.
public abstract class MaterialBibliografico implements Prestable {

    // ENCAPSULACIÓN: Atributos protegidos accesibles dentro de la jerarquía de herencia y mediante getters/setters.
    protected String idMaterial;
    protected String titulo;
    protected String autor;
    protected int anioPublicacion;
    protected int ejemplaresDisponibles;
    protected boolean activo;

    /**
     * Constructor base para registrar un material en el catálogo.
     * Inicia con estado activo (true) para soportar baja lógica.
     *
     * @param idMaterial            Código identificador único del material.
     * @param titulo                Título de la obra o publicación.
     * @param autor                 Autor, editor o responsable principal.
     * @param anioPublicacion       Año de publicación.
     * @param ejemplaresDisponibles Cantidad inicial de copias físicas disponibles.
     */
    public MaterialBibliografico(String idMaterial, String titulo, String autor, int anioPublicacion, int ejemplaresDisponibles) {
        this.idMaterial = idMaterial;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        setEjemplaresDisponibles(ejemplaresDisponibles);
        this.activo = true;
    }

    /**
     * Método polimórfico que construye la ficha catalográfica detallada de cada subclase.
     *
     * @return Cadena formateada con los metadatos específicos del material.
     */
    // POLIMORFISMO: Obliga a cada tipo de material a presentar sus propios atributos específicos.
    public abstract String mostrarFicha();

    /**
     * Verifica si el material está activo y cuenta con copias disponibles para préstamo (RN-01).
     *
     * @return true si hay al menos un ejemplar disponible y el registro está activo.
     */
    public boolean hayDisponibilidad() {
        return this.activo && this.ejemplaresDisponibles > 0;
    }

    /**
     * Disminuye en una unidad el inventario disponible al concretarse un préstamo.
     */
    public void reducirStock() {
        if (this.ejemplaresDisponibles > 0) {
            this.ejemplaresDisponibles--;
        }
    }

    /**
     * Incrementa en una unidad el inventario disponible al procesarse una devolución.
     */
    public void incrementarStock() {
        this.ejemplaresDisponibles++;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(String idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        if (ejemplaresDisponibles >= 0) {
            this.ejemplaresDisponibles = ejemplaresDisponibles;
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return mostrarFicha();
    }
}