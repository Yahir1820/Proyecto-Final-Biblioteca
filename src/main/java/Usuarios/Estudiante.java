package Usuarios;

public class Estudiante extends Lector{
    private String matricula;
    private String carrera;

    public Estudiante(String idUsuario, String nombre, String correo, String matricula, String carrera){
        super(idUsuario, nombre, correo);
        this.matricula = matricula;
        this.carrera = carrera;
    }

    @Override
    public String getRol(){
        return "Estudiante";
    }

    @Override
    public int getLimitePrestamos(){
        return 80;
    }

    @Override
    public int getDiasExtra(){
        return 7;
    }
}
