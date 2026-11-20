package Usuarios;

/**
 * Clase abstracta intermedia que agrupa a los usuarios con derecho a préstamo y control de adeudos.
 */
public abstract class Lector extends Usuario {
    protected double adeudoMultas;

    /**
     * Constructor base para lectores. Inicializa el saldo de multas en cero.
     *
     * @param idUsuario Identificador único del lector.
     * @param nombre Nombre completo del lector.
     * @param correo Correo electrónico de contacto.
     */
    public Lector(String idUsuario, String nombre, String correo){
        super(idUsuario, nombre, correo);
    }

    /**
     * Define el límite máximo de materiales que el lector puede tener prestados al mismo tiempo.
     *
     * @return Cantidad máxima de materiales permitidos.
     */
    public abstract int getLimitePrestamos();

    /**
     * Define los días adicionales sobre el plazo base del material según el perfil del lector.
     *
     * @return Días extra de préstamo.
     */
    public abstract int getDiasExtra();

    /**
     * Acumula un monto de multa al saldo del lector por entregas tardías.
     *
     * @param multa Cantidad monetaria a sumar al adeudo.
     */
    public void agregarAdeudoMultas(double multa){
        if(adeudoMultas > 0){
            this.adeudoMultas += multa;
        }
    }

    //pago total de las multas pendientes
    public void liquidarAdeudo(){
        this.adeudoMultas = 0.0;
    }

    /**
     * Verifica si el lector tiene multas pendientes de pago (RN-04 y RN-06).
     *
     * @return true si el adeudo es mayor a 0; false en caso contrario.
     */
    public boolean tieneAdeudos() {
        return this.adeudoMultas > 0;
    }

    public double getAdeudoMultas() {
        return adeudoMultas;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Adeudo: $%.2f MXN | Límite préstamos: %d",
                adeudoMultas, getLimitePrestamos());
    }
}
