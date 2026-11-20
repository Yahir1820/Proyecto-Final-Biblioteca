package Usuarios;

public abstract class Lector extends Usuario {
    protected double adeudoMultas;

    public Lector(String idUsuario, String nombre, String correo){
        super(idUsuario, nombre, correo);
    }

    public abstract int getLimitePrestamos();
    public abstract int getDiasExtra();

    public void agregarAdeudoMultas(double multa){
        this.adeudoMultas = multa;
    }

    public void liquidarAdeudo(){
        this.adeudoMultas = 0;
    }
}
