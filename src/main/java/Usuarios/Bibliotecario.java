package Usuarios;

/**
 * Representa al personal administrativo encargado de operar el sistema y autorizar préstamos.
 */
public class Bibliotecario extends Usuario{
    private final String numeroEmpleado;
    private String turno;

    /**
     * Constructor para registrar un bibliotecario.
     *
     * @param idUsuario      Identificador único en el sistema.
     * @param nombre         Nombre completo del bibliotecario.
     * @param correo         Correo electrónico laboral.
     * @param numeroEmpleado Número de empleado institucional.
     * @param turno          Turno asignado (Matutino, Vespertino, Mixto).
     */
    public Bibliotecario(String idUsuario, String nombre, String correo, String numeroEmpleado, String turno){
        super(idUsuario, nombre, correo);
        this.numeroEmpleado = numeroEmpleado;
        this.turno = turno;
    }

    // implementación específica del método polimórfico
    @Override
    public String getRol() {
        return "Bibliotecario";
    }

    public String getNumeroEmpleado(){
        return numeroEmpleado;
    }

    public String getTurno(){
        return turno;
    }

    public void setTurno(String turno){
        this.turno = turno;
    }

    //muestra de información utilizando herencia
    @Override
    public String toString() {
        return super.toString() + String.format(" | No. Empleado: %s | Turno: %s", numeroEmpleado, turno);
    }
}
