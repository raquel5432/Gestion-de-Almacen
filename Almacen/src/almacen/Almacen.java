package almacen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Almacen {

    public static final Path ARCHIVO_DEFECTO = Paths.get("inventario.csv");
    private static final String SEPARADOR = ";";
    private static final String ENCABEZADO = "tipo;id;nombre;cantidad;precio;extra1;extra2";

    private final List<Producto> inventario = new ArrayList<>();

    public boolean existeId(String id) {
        return buscar(id) != null;
    }

    public Producto buscar(String id) {
        for (Producto p : inventario) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public void agregar(Producto p) {
        if (existeId(p.getId())) {
            throw new IllegalArgumentException("Ya existe un producto con el ID \"" + p.getId() + "\".");
        }
        inventario.add(p);
    }

    public void reemplazar(String idOriginal, Producto nuevo) {
        for (int i = 0; i < inventario.size(); i++) {
            if (inventario.get(i).getId().equalsIgnoreCase(idOriginal)) {
                inventario.set(i, nuevo);
                return;
            }
        }
        throw new IllegalArgumentException("No se encontró el producto \"" + idOriginal + "\".");
    }

    public boolean eliminar(String id) {
        return inventario.removeIf(p -> p.getId().equalsIgnoreCase(id));
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(inventario);
    }

    public List<Producto> filtrar(String texto, String tipo) {
        String criterio = texto == null ? "" : texto.trim().toLowerCase();
        boolean todos = tipo == null || tipo.equals("Todos");
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : inventario) {
            boolean coincideTipo = todos || p.getTipo().equals(tipo);
            boolean coincideTexto = criterio.isEmpty()
                    || p.getId().toLowerCase().contains(criterio)
                    || p.getNombre().toLowerCase().contains(criterio);
            if (coincideTipo && coincideTexto) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public int getTotalProductos() {
        return inventario.size();
    }

    public int getTotalUnidades() {
        int total = 0;
        for (Producto p : inventario) {
            total += p.getCantidad();
        }
        return total;
    }

    public double getCostoTotal() {
        double total = 0.0;
        for (Producto p : inventario) {
            total += p.calcularCosto();
        }
        return total;
    }

    public int getTotalAlertas() {
        int total = 0;
        for (Producto p : inventario) {
            if (!p.getAlerta().isEmpty()) {
                total++;
            }
        }
        return total;
    }

    public String generarReporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("=============================================\n");
        sb.append("      REPORTE DE INVENTARIO DEL ALMACÉN\n");
        sb.append("=============================================\n\n");
        if (inventario.isEmpty()) {
            sb.append("  El inventario está vacío.\n");
            return sb.toString();
        }
        for (Producto p : inventario) {
            sb.append(p.getInfo()).append("\n");
        }
        sb.append("---------------------------------------------\n");
        sb.append(String.format("  COSTO TOTAL DEL INVENTARIO: $%,.2f%n", getCostoTotal()));
        sb.append(String.format("  TOTAL DE PRODUCTOS REGISTRADOS: %d%n", inventario.size()));
        sb.append(String.format("  TOTAL DE UNIDADES: %d%n", getTotalUnidades()));
        sb.append(String.format("  PRODUCTOS CON ALERTA: %d%n", getTotalAlertas()));
        sb.append("---------------------------------------------\n");
        return sb.toString();
    }

    public void guardar() throws IOException {
        guardar(ARCHIVO_DEFECTO);
    }

    public void guardar(Path destino) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);
        for (Producto p : inventario) {
            String[] extras = p.getExtras();
            lineas.add(String.join(SEPARADOR,
                    limpiar(p.getTipo()),
                    limpiar(p.getId()),
                    limpiar(p.getNombre()),
                    String.valueOf(p.getCantidad()),
                    String.format(Locale.ROOT, "%.4f", p.getPrecio()),
                    limpiar(extras[0]),
                    limpiar(extras[1])));
        }
        Files.write(destino, lineas, StandardCharsets.UTF_8);
    }

    public void cargar() throws IOException {
        cargar(ARCHIVO_DEFECTO);
    }

    public void cargar(Path origen) throws IOException {
        List<String> lineas = Files.readAllLines(origen, StandardCharsets.UTF_8);
        List<Producto> cargados = new ArrayList<>();
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) {
                continue;
            }
            cargados.add(crearDesdeLinea(linea, i + 1));
        }
        inventario.clear();
        inventario.addAll(cargados);
    }

    public void cargarDemo() {
        agregar(new ProductoPerecedero("P001", "Leche entera", 50, 1.20, 5, 4.0));
        agregar(new ProductoPerecedero("P002", "Yogur natural", 30, 0.85, 2, 2.0));
        agregar(new ProductoElectronico("E001", "Televisor 55\"", 10, 450.00, 24, 110));
        agregar(new ProductoElectronico("E002", "Aire acondicionado", 5, 650.00, 36, 220));
        agregar(new MateriaPrima("M001", "Harina de trigo", 200, 0.45, "kg", "Molinos SA"));
        agregar(new MateriaPrima("M002", "Aceite de girasol", 80, 1.30, "lt", "AceitesCorp"));
    }

    private Producto crearDesdeLinea(String linea, int numero) throws IOException {
        String[] c = linea.split(SEPARADOR, -1);
        if (c.length < 7) {
            throw new IOException("Formato inválido en la línea " + numero + ".");
        }
        try {
            String id = c[1];
            String nombre = c[2];
            int cantidad = Integer.parseInt(c[3]);
            double precio = Double.parseDouble(c[4]);
            switch (c[0]) {
                case "Perecedero":
                    return new ProductoPerecedero(id, nombre, cantidad, precio,
                            Integer.parseInt(c[5]), Double.parseDouble(c[6]));
                case "Electrónico":
                    return new ProductoElectronico(id, nombre, cantidad, precio,
                            Integer.parseInt(c[5]), Integer.parseInt(c[6]));
                case "Materia Prima":
                    return new MateriaPrima(id, nombre, cantidad, precio, c[5], c[6]);
                default:
                    throw new IOException("Tipo desconocido en la línea " + numero + ": " + c[0]);
            }
        } catch (NumberFormatException e) {
            throw new IOException("Número inválido en la línea " + numero + ".");
        }
    }

    private String limpiar(String texto) {
        return texto.replace(SEPARADOR, ",").replace("\n", " ").replace("\r", " ");
    }
}
