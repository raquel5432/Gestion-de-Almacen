package almacen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;

public final class Tema {

    public static final Color NEGRO = new Color(0x0B1320);
    public static final Color AZUL = new Color(0x1F3A5F);
    public static final Color CLARO = new Color(0xF7F5F2);
    public static final Color AMBAR = new Color(0xFFB703);
    public static final Color TEAL = new Color(0x2A9D8F);
    public static final Color BORDE = new Color(0xDDD9D2);
    public static final Color TEXTO_SUAVE = new Color(0xB8C2D1);
    public static final Color FILA_AMBAR = new Color(0xFFEFC2);
    public static final Color FILA_ROJA = new Color(0xFFDAD4);
    public static final Color ALERTA = new Color(0xB3261E);

    private Tema() {
    }

    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }
        for (Object clave : Collections.list(UIManager.getDefaults().keys())) {
            Object valor = UIManager.get(clave);
            if (valor instanceof FontUIResource) {
                UIManager.put(clave, new FontUIResource("SansSerif", Font.PLAIN, 13));
            }
        }
        UIManager.put("Panel.background", CLARO);
        UIManager.put("OptionPane.background", CLARO);
        UIManager.put("OptionPane.messageForeground", NEGRO);
        UIManager.put("Label.foreground", NEGRO);
        UIManager.put("CheckBox.background", CLARO);
        UIManager.put("TextField.selectionBackground", TEAL);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("FormattedTextField.selectionBackground", TEAL);
        UIManager.put("FormattedTextField.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", TEAL);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ScrollBar.thumb", AZUL);
        UIManager.put("ScrollBar.background", CLARO);
    }

    public static JButton boton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setBorder(new CompoundBorder(new LineBorder(fondo.darker(), 1), new EmptyBorder(7, 14, 7, 14)));
        prepararBoton(b, aclarar(fondo));
        return b;
    }

    public static JButton botonContorno(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(Color.WHITE);
        b.setForeground(AZUL);
        b.setBorder(new CompoundBorder(new LineBorder(AZUL, 1), new EmptyBorder(7, 14, 7, 14)));
        prepararBoton(b, new Color(0xEDEAE4));
        return b;
    }

    public static void campo(JTextField t) {
        t.setBackground(Color.WHITE);
        t.setForeground(NEGRO);
        t.setCaretColor(NEGRO);
        t.setBorder(new CompoundBorder(new LineBorder(BORDE, 1), new EmptyBorder(5, 8, 5, 8)));
    }

    private static void prepararBoton(JButton b, Color encima) {
        Color normal = b.getBackground();
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(encima);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(normal);
            }
        });
    }

    private static Color aclarar(Color c) {
        int r = c.getRed() + (int) ((255 - c.getRed()) * 0.18);
        int g = c.getGreen() + (int) ((255 - c.getGreen()) * 0.18);
        int b = c.getBlue() + (int) ((255 - c.getBlue()) * 0.18);
        return new Color(r, g, b);
    }
}
