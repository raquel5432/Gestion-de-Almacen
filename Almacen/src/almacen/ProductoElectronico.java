package almacen;

public class ProductoElectronico extends Producto {

    private int garantiaMeses;
    private int voltaje;

    public ProductoElectronico(String id, String nombre,
                               int cantidad, double precio,
                               int garantiaMeses, int voltaje) {
        super(id, nombre, cantidad, precio);
        this.garantiaMeses = garantiaMeses;
        this.voltaje = voltaje;
    }

    public int getGarantiaMeses() {
        return garantiaMeses;
    }

    public int getVoltaje() {
        return voltaje;
    }

    @Override
    public String getTipo() {
        return "Electrónico";
    }

    @Override
    public String getDetalle() {
        return garantiaMeses + " meses de garantía · " + voltaje + "V";
    }

    @Override
    public String[] getExtras() {
        return new String[]{String.valueOf(garantiaMeses), String.valueOf(voltaje)};
    }

    @Override
    public double calcularCosto() {
        double aniosGarantia = garantiaMeses / 12.0;
        return precio * cantidad * (1 + (0.10 * aniosGarantia));
    }

    @Override
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("[ELECTRÓNICO] ").append(nombre).append(" | ID: ").append(id).append("\n");
        info.append("Cantidad: ").append(cantidad).append(" unidades\n");
        info.append(String.format("Precio unitario: $%,.2f%n", precio));
        info.append("Garantía: ").append(garantiaMeses).append(" meses\n");
        info.append("Voltaje: ").append(voltaje).append("V\n");
        info.append(String.format("Costo total: $%,.2f%n", calcularCosto()));
        if (voltaje == 220) {
            info.append("Requiere instalación especializada\n");
        }
        return info.toString();
    }
}
