package Usuarios;

/**
 * Representa a un lector con perfil académico docente, con mayor cupo y plazo extendido.
 */
public class Docente extends Lector{
    public static final int LIMITE_PRESTAMOS_DOCENTE = 5;
    public static final int DIAS_EXTRA_DOCENTE = 3;

    private String numeroPersonal;
    private String facultad;

    /**
     * Constructor para registrar un docente.
     *
     * @param idUsuario Identificador único en el sistema.
     * @param nombre Nombre completo del docente.
     * @param correo Correo institucional.
     * @param numeroPersonal Número de personal académico.
     * @param facultad Facultad o departamento de adscripción.
     */
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
        return LIMITE_PRESTAMOS_DOCENTE;
    }

    @Override
    public int getDiasExtra(){
        return DIAS_EXTRA_DOCENTE;
    }

    public String getNumeroPersonal() {
        return numeroPersonal;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | No. Personal: %s | Facultad: %s", numeroPersonal, facultad);
    }
}
