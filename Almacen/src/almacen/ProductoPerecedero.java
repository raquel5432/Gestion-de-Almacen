package almacen;

public class ProductoPerecedero extends Producto {

    private int diasVigencia;
    private double temperatura;

    public ProductoPerecedero(String id, String nombre,
                              int cantidad, double precio,
                              int diasVigencia, double temperatura) {
        super(id, nombre, cantidad, precio);
        this.diasVigencia = diasVigencia;
        this.temperatura = temperatura;
    }

    public int getDiasVigencia() {
        return diasVigencia;
    }

    public double getTemperatura() {
        return temperatura;
    }

    @Override
    public String getTipo() {
        return "Perecedero";
    }

    @Override
    public String getDetalle() {
        return diasVigencia + " días de vigencia · " + temperatura + " °C";
    }

    @Override
    public String[] getExtras() {
        return new String[]{String.valueOf(diasVigencia), String.valueOf(temperatura)};
    }

    @Override
    protected String getAlertaEspecifica() {
        return diasVigencia < 3 ? "Próximo a vencer" : "";
    }

    @Override
    public double calcularCosto() {
        return precio * cantidad * (1 + (0.05 * diasVigencia));
    }

    @Override
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("[PERECEDERO] ").append(nombre).append(" | ID: ").append(id).append("\n");
        info.append("Cantidad: ").append(cantidad).append(" unidades\n");
        info.append(String.format("Precio unitario: $%,.2f%n", precio));
        info.append("Días de vigencia: ").append(diasVigencia).append("\n");
        info.append("Temperatura: ").append(temperatura).append(" °C\n");
        info.append(String.format("Costo total: $%,.2f%n", calcularCosto()));
        if (diasVigencia < 3) {
            info.append("⚠ PRODUCTO PRÓXIMO A VENCER\n");
        }
        return info.toString();
    }
}
