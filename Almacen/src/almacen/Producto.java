package almacen;

public abstract class Producto {

    public static final int UMBRAL_STOCK_BAJO = 10;

    protected String id;
    protected String nombre;
    protected int cantidad;
    protected double precio;

    public Producto(String id, String nombre, int cantidad, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getAlerta() {
        String especifica = getAlertaEspecifica();
        String stock = "";
        if (cantidad == 0) {
            stock = "Sin stock";
        } else if (cantidad <= UMBRAL_STOCK_BAJO) {
            stock = "Stock bajo";
        }
        if (stock.isEmpty()) {
            return especifica;
        }
        if (especifica.isEmpty()) {
            return stock;
        }
        return stock + " · " + especifica;
    }

    protected String getAlertaEspecifica() {
        return "";
    }

    public abstract String getTipo();

    public abstract String getDetalle();

    public abstract String[] getExtras();

    public abstract String getInfo();

    public abstract double calcularCosto();
}
