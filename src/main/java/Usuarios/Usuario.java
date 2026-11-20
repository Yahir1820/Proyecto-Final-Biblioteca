package Usuarios;

/**
 * Clase abstracta base que representa a cualquier persona registrada en el sistema bibliotecario.
 */
public abstract class Usuario {
    protected final String idUsuario;
    protected String nombre;
    protected String correo;
    protected boolean activo;

    /**
     * Constructor base para inicializar los datos generales de un usuario.
     * Todo usuario inicia con estado activo (true) para gestionar la baja lógica.
     *
     * @param idUsuario Identificador único del usuario.
     * @param nombre    Nombre completo del usuario.
     * @param correo    Correo electrónico institucional o personal.
     */
    public Usuario(String idUsuario, String nombre, String correo){
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        this.idUsuario = idUsuario;
        setNombre(nombre);
        setCorreo(correo);
        this.activo = true;
    }

    /**
     * Método polimórfico que devuelve el rol específico del usuario dentro de la biblioteca.
     *
     * @return Nombre del rol (Bibliotecario, Estudiante o Docente).
     */
    // método abstracto que obliga a cada subclase a definir su propio comportamiento.
    public abstract String getRol();

    public String getIdUsuario(){
        return idUsuario;
    }

    public String getNombre(){
        return nombre;
    }

    //validaciones de campos obligatorios
    public void setNombre(String nombre){
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public String getCorreo(){
        return correo;
    }

    //validaciones de campos obligatorios
    public void setCorreo(String correo) {
        if (correo == null || !correo.contains("@")){
            throw new IllegalArgumentException("Correo inválido");
        }
        this.correo = correo.trim();
    }

    public boolean isActivo(){
        return activo;
    }

    //métodos de comportamiento de estados (útil en préstamos)
    public void activar(){
        this.activo = true;
    }

    public void desactivar(){
        this.activo = false;
    }

    @Override
    public String toString() {
        String estado = activo ? "Activo" : "Inactivo";
        return String.format("[%s] ID: %s | Nombre: %s | Correo: %s | Estado: %s",
                getRol(), idUsuario, nombre, correo, estado);
    }
}
