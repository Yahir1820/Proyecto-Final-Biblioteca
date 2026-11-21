package Servicios;

public class GeneradorComprobante {
    public String generarTicketPrestamo(Prestamo prestamo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("====================================================\n");
        stringBuilder.append("              COMPROBANTE DE PRÉSTAMO               \n");
        stringBuilder.append("====================================================\n");
        stringBuilder.append("Folio               : ").append(prestamo.getFolio()).append("\n");
        stringBuilder.append("Fecha del préstamo  : ").append(prestamo.getFechaPrestamo()).append("\n");
        stringBuilder.append("Fecha límite        : ").append(prestamo.getFechaDevolucionEsperada()).append("\n");
        stringBuilder.append("Lector              : ").append(prestamo.getLector().getNombre()).append("\n");
        stringBuilder.append("Autorizado por      : ").append(prestamo.getBibliotecario().getNombre()).append("\n");
        stringBuilder.append("----------------------------------------------------\n");
        stringBuilder.append("Materiales prestados:\n");
        for (DetallePrestamo detalle : prestamo.getDetalles()) {
            stringBuilder.append(detalle.toString()).append("\n");
        }
        stringBuilder.append("====================================================\n");
        return stringBuilder.toString();
    }

    public String generarTicketDevolucion(Prestamo prestamo, double multaTotal) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("====================================================\n");
        stringBuilder.append("             COMPROBANTE DE DEVOLUCIÓN              \n");
        stringBuilder.append("====================================================\n");
        stringBuilder.append("Folio             : ").append(prestamo.getFolio()).append("\n");
        stringBuilder.append("Lector            : ").append(prestamo.getLector().getNombre()).append("\n");
        stringBuilder.append("Fecha límite      : ").append(prestamo.getFechaDevolucionEsperada()).append("\n");
        stringBuilder.append("Fecha de entrega  : ").append(prestamo.getFechaDevolucionReal()).append("\n");
        stringBuilder.append("----------------------------------------------------\n");
        stringBuilder.append("Detalle de materiales devueltos:\n");
        for (DetallePrestamo detalle : prestamo.getDetalles()) {
            stringBuilder.append(detalle.toString()).append("\n");
        }
        stringBuilder.append("----------------------------------------------------\n");
        stringBuilder.append(String.format("Multa generada  : $%.2f MXN\n", multaTotal));
        stringBuilder.append(String.format("Adeudo actual   : $%.2f MXN\n", prestamo.getLector().getAdeudoMultas()));
        stringBuilder.append("====================================================\n");
        return stringBuilder.toString();
    }
}
