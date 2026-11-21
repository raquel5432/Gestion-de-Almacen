package almacen;

public class MateriaPrima extends Producto {

    private String unidadMedida;
    private String proveedor;

    public MateriaPrima(String id, String nombre,
                        int cantidad, double precio,
                        String unidadMedida, String proveedor) {
        super(id, nombre, cantidad, precio);
        this.unidadMedida = unidadMedida;
        this.proveedor = proveedor;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public String getProveedor() {
        return proveedor;
    }

    @Override
    public String getTipo() {
        return "Materia Prima";
    }

    @Override
    public String getDetalle() {
        return unidadMedida + " · " + proveedor;
    }

    @Override
    public String[] getExtras() {
        return new String[]{unidadMedida, proveedor};
    }

    @Override
    public double calcularCosto() {
        return precio * cantidad;
    }

    @Override
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("[MATERIA PRIMA] ").append(nombre).append(" | ID: ").append(id).append("\n");
        info.append("Cantidad: ").append(cantidad).append(" ").append(unidadMedida).append("\n");
        info.append(String.format("Precio unitario: $%,.2f%n", precio));
        info.append("Proveedor: ").append(proveedor).append("\n");
        info.append(String.format("Costo total: $%,.2f%n", calcularCosto()));
        return info.toString();
    }
}
