package Usuarios;

/**
 * Representa a un lector con perfil de estudiante inscrito.
 */
public class Estudiante extends Lector{
    public static final int LIMITE_PRESTAMOS_ESTUDIANTE = 3;
    public static final int DIAS_EXTRA_ESTUDIANTE = 1;

    private String matricula;
    private String carrera;

    /**
     * Constructor para registrar un estudiante.
     * @param idUsuario Identificador único en el sistema.
     * @param nombre Nombre completo del estudiante.
     * @param correo Correo institucional.
     * @param matricula Matrícula académica.
     * @param carrera Programa educativo o carrera que cursa.
     */
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
        return LIMITE_PRESTAMOS_ESTUDIANTE;
    }

    @Override
    public int getDiasExtra(){
        return DIAS_EXTRA_ESTUDIANTE;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera){
        this.carrera = carrera;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Matrícula: %s | Carrera: %s", matricula, carrera);
    }
}
