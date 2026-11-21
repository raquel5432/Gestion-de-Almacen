package almacen;

import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Tema.instalar();

        SwingUtilities.invokeLater(() -> {
            Almacen almacen = new Almacen();
            try {
                if (Files.exists(Almacen.ARCHIVO_DEFECTO)) {
                    almacen.cargar();
                } else {
                    almacen.cargarDemo();
                    almacen.guardar();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo leer inventario.csv: " + ex.getMessage(),
                        "Error al cargar", JOptionPane.ERROR_MESSAGE);
            }
            new VentanaPrincipal(almacen).setVisible(true);
        });
    }
}
