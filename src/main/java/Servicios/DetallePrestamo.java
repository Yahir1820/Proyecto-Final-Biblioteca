package Servicios;

import Materiales.MaterialBibliografico;

public class DetallePrestamo {
    protected MaterialBibliografico material;
    protected int cantidad;
    protected double subtotalMulta;

    public DetallePrestamo(MaterialBibliografico material, int cantidad) {
        this.material = material;
        this.cantidad = cantidad;
        this.subtotalMulta = 0.0;
    }

    public double calcularMultaItem(int diasRetraso) {
        if (diasRetraso <= 0) {
            this.subtotalMulta = 0.0;
        } else {
            this.subtotalMulta = material.calcularMulta(diasRetraso) * cantidad;
        }
        return this.subtotalMulta;
    }

    public MaterialBibliografico getMaterial() {
        return material;
    }

    public void setMaterial(MaterialBibliografico material) {
        this.material = material;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotalMulta() {
        return subtotalMulta;
    }

    @Override
    public String toString() {
        return String.format("  - [%s] %s | Cant: %d | Multa ítem: $%.2f MXN",
                material.getIdMaterial(), material.getTitulo(), cantidad, subtotalMulta);
    }
}
