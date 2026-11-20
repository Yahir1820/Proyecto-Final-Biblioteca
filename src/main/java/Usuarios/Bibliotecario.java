package Usuarios;

public class Bibliotecario extends Usuario{
    private String numeroEmpleado;
    private String turno;

    public Bibliotecario(String idUsuario, String nombre, String correo, String numeroEmpleado, String turno){
        super(idUsuario, nombre, correo);
        this.numeroEmpleado = numeroEmpleado;
        this.turno = turno;
    }

    @Override
    public String getRol() {
        return "Bibliotecario";
    }
    public String getNumeroEmpleado(){
        return numeroEmpleado;
    }
}
