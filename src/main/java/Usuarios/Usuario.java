package Usuarios;

public abstract class Usuario {
    protected String idUsuario;
    protected String nombre;
    protected String correo;
    protected boolean activo;

    public Usuario(String idUsuario, String nombre, String correo){
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.activo = true;
    }

    public abstract String getRol();

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
