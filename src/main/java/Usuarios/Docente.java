package Usuarios;

public class Docente extends Lector{
    private String numeroPersonal;
    private String facultad;

    public Docente(String idUsuario, String nombre, String correo, String numeroPersonal, String facultad){
        super(idUsuario, nombre, correo);
        this.numeroPersonal = numeroPersonal;
        this.facultad = facultad;
    }

    @Override
    public String getRol() {
        return "Docente";
    }

    @Override
    public int getLimitePrestamos(){
        return 100;
    }

    @Override
    public int getDiasExtra(){
        return 7;
    }
}
