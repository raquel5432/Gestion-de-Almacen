package almacen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class VentanaPrincipal extends JFrame {

    private static final String[] COLUMNAS = {"ID", "Tipo", "Nombre", "Cantidad", "Precio", "Detalle", "Costo total", "Alerta"};
    private static final int COL_ALERTA = 7;

    private final Almacen almacen;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField txtBuscar = new JTextField(22);
    private final JComboBox<String> cmbFiltro =
            new JComboBox<>(new String[]{"Todos", "Perecedero", "Electrónico", "Materia Prima"});
    private final JLabel lblProductos = crearEstadistica();
    private final JLabel lblUnidades = crearEstadistica();
    private final JLabel lblCosto = crearEstadistica();
    private final JLabel lblAlertas = crearEstadistica();
    private final JLabel lblMostrando = new JLabel(" ");

    public VentanaPrincipal(Almacen almacen) {
        super("Sistema de Inventario de Almacén");
        this.almacen = almacen;

        modelo = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                switch (columna) {
                    case 3:
                        return Integer.class;
                    case 4:
                    case 6:
                        return Double.class;
                    default:
                        return String.class;
                }
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowSorter(new TableRowSorter<>(modelo));
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(Color.WHITE);
        RenderizadorFilas renderizador = new RenderizadorFilas();
        tabla.setDefaultRenderer(Object.class, renderizador);
        tabla.setDefaultRenderer(String.class, renderizador);
        tabla.setDefaultRenderer(Integer.class, renderizador);
        tabla.setDefaultRenderer(Double.class, renderizador);
        tabla.getTableHeader().setDefaultRenderer(new RenderizadorEncabezado());
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tabla.getTableHeader().setReorderingAllowed(false);
        ajustarAnchos();

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    editarProducto();
                }
            }
        });
        tabla.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    eliminarProducto();
                }
            }
        });

        Tema.campo(txtBuscar);
        cmbFiltro.setBackground(Color.WHITE);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refrescar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refrescar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refrescar();
            }
        });
        cmbFiltro.addActionListener(e -> refrescar());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(new EmptyBorder(4, 20, 0, 20));
        centro.add(scroll, BorderLayout.CENTER);

        getContentPane().setBackground(Tema.CLARO);
        setLayout(new BorderLayout());
        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardar();
                dispose();
                System.exit(0);
            }
        });

        setMinimumSize(new Dimension(1050, 600));
        setSize(1180, 680);
        setLocationRelativeTo(null);
        refrescar();
    }

    private JPanel construirPanelSuperior() {
        JLabel titulo = new JLabel("Inventario de Almacén");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitulo = new JLabel("Control de productos, stock y costos");
        subtitulo.setForeground(Tema.TEXTO_SUAVE);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Tema.NEGRO);
        cabecera.setBorder(new CompoundBorder(new MatteBorder(0, 0, 4, 0, Tema.AMBAR),
                new EmptyBorder(16, 20, 14, 20)));
        cabecera.add(textos, BorderLayout.WEST);

        JButton btnAgregar = Tema.boton("+ Agregar", Tema.AMBAR, Tema.NEGRO);
        JButton btnEditar = Tema.boton("Editar", Tema.AZUL, Color.WHITE);
        JButton btnEliminar = Tema.boton("Eliminar", Tema.NEGRO, Color.WHITE);
        JButton btnEntrada = Tema.boton("Entrada de stock", Tema.TEAL, Color.WHITE);
        JButton btnSalida = Tema.boton("Salida de stock", Tema.TEAL, Color.WHITE);
        JButton btnDetalle = Tema.botonContorno("Ver detalle");
        JButton btnReporte = Tema.botonContorno("Reporte");
        JButton btnExportar = Tema.botonContorno("Exportar CSV");

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnEntrada.addActionListener(e -> ajustarStock(true));
        btnSalida.addActionListener(e -> ajustarStock(false));
        btnDetalle.addActionListener(e -> verDetalle());
        btnReporte.addActionListener(e -> verReporte());
        btnExportar.addActionListener(e -> exportar());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        acciones.setOpaque(false);
        acciones.add(btnAgregar);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);
        acciones.add(Box.createHorizontalStrut(14));
        acciones.add(btnEntrada);
        acciones.add(btnSalida);
        acciones.add(Box.createHorizontalStrut(14));
        acciones.add(btnDetalle);
        acciones.add(btnReporte);
        acciones.add(btnExportar);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busqueda.setOpaque(false);
        busqueda.add(new JLabel("Buscar:"));
        busqueda.add(txtBuscar);
        busqueda.add(Box.createHorizontalStrut(10));
        busqueda.add(new JLabel("Tipo:"));
        busqueda.add(cmbFiltro);

        JPanel controles = new JPanel(new GridLayout(2, 1, 0, 10));
        controles.setOpaque(false);
        controles.setBorder(new EmptyBorder(14, 14, 10, 14));
        controles.add(acciones);
        controles.add(busqueda);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(controles, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelInferior() {
        JPanel resumen = new JPanel(new GridLayout(1, 4, 14, 0));
        resumen.setOpaque(false);
        resumen.add(tarjeta("PRODUCTOS", lblProductos, Tema.AZUL));
        resumen.add(tarjeta("UNIDADES EN STOCK", lblUnidades, Tema.TEAL));
        resumen.add(tarjeta("COSTO TOTAL DEL INVENTARIO", lblCosto, Tema.AMBAR));
        resumen.add(tarjeta("PRODUCTOS CON ALERTA", lblAlertas, Tema.NEGRO));

        lblMostrando.setForeground(Tema.AZUL);
        JLabel leyenda = new JLabel("Amarillo: stock bajo (≤ " + Producto.UMBRAL_STOCK_BAJO
                + ")   ·   Rojo: sin stock o próximo a vencer");
        leyenda.setForeground(Tema.AZUL);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(lblMostrando, BorderLayout.WEST);
        pie.add(leyenda, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));
        panel.add(resumen, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel tarjeta(String titulo, JLabel valor, Color acento) {
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(Tema.AZUL);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 11f));

        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(Tema.BORDE, 1),
                new CompoundBorder(new MatteBorder(4, 0, 0, 0, acento), new EmptyBorder(8, 14, 10, 14))));
        p.add(etiqueta, BorderLayout.NORTH);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    private static JLabel crearEstadistica() {
        JLabel l = new JLabel("0", SwingConstants.LEFT);
        l.setForeground(Tema.NEGRO);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 24f));
        return l;
    }

    private void ajustarAnchos() {
        int[] anchos = {70, 110, 200, 80, 90, 240, 110, 170};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void refrescar() {
        modelo.setRowCount(0);
        java.util.List<Producto> lista =
                almacen.filtrar(txtBuscar.getText(), (String) cmbFiltro.getSelectedItem());
        for (Producto p : lista) {
            modelo.addRow(new Object[]{
                p.getId(), p.getTipo(), p.getNombre(), p.getCantidad(),
                p.getPrecio(), p.getDetalle(), p.calcularCosto(), p.getAlerta()
            });
        }
        lblProductos.setText(String.valueOf(almacen.getTotalProductos()));
        lblUnidades.setText(String.format("%,d", almacen.getTotalUnidades()));
        lblCosto.setText(String.format("$%,.2f", almacen.getCostoTotal()));
        lblAlertas.setText(String.valueOf(almacen.getTotalAlertas()));
        lblAlertas.setForeground(almacen.getTotalAlertas() > 0 ? Tema.ALERTA : Tema.NEGRO);
        lblMostrando.setText("Mostrando " + lista.size() + " de " + almacen.getTotalProductos());
    }

    private Producto productoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        String id = (String) modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0);
        return almacen.buscar(id);
    }

    private void seleccionarPorId(String id) {
        for (int i = 0; i < tabla.getRowCount(); i++) {
            if (id.equalsIgnoreCase((String) tabla.getValueAt(i, 0))) {
                tabla.setRowSelectionInterval(i, i);
                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void agregarProducto() {
        DialogProducto dialogo = new DialogProducto(this, almacen, null);
        dialogo.setVisible(true);
        Producto nuevo = dialogo.getResultado();
        if (nuevo == null) {
            return;
        }
        try {
            almacen.agregar(nuevo);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        guardar();
        refrescar();
        seleccionarPorId(nuevo.getId());
    }

    private void editarProducto() {
        Producto actual = productoSeleccionado();
        if (actual == null) {
            return;
        }
        DialogProducto dialogo = new DialogProducto(this, almacen, actual);
        dialogo.setVisible(true);
        Producto editado = dialogo.getResultado();
        if (editado == null) {
            return;
        }
        almacen.reemplazar(actual.getId(), editado);
        guardar();
        refrescar();
        seleccionarPorId(editado.getId());
    }

    private void eliminarProducto() {
        Producto p = productoSeleccionado();
        if (p == null) {
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + p.getNombre() + "\" (" + p.getId() + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            almacen.eliminar(p.getId());
            guardar();
            refrescar();
        }
    }

    private void ajustarStock(boolean entrada) {
        Producto p = productoSeleccionado();
        if (p == null) {
            return;
        }
        int maximo = entrada ? 10000000 : p.getCantidad();
        if (!entrada && maximo == 0) {
            JOptionPane.showMessageDialog(this, "Este producto no tiene stock disponible.",
                    "Sin stock", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, maximo, 1));
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.add(new JLabel(p.getNombre() + " — stock actual: " + p.getCantidad()));
        panel.add(new JLabel(entrada ? "Unidades a ingresar:" : "Unidades a retirar:"));
        panel.add(spinner);
        int r = JOptionPane.showConfirmDialog(this, panel,
                entrada ? "Entrada de stock" : "Salida de stock",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        int unidades = ((Number) spinner.getValue()).intValue();
        p.setCantidad(entrada ? p.getCantidad() + unidades : p.getCantidad() - unidades);
        guardar();
        refrescar();
        seleccionarPorId(p.getId());
    }

    private void verDetalle() {
        Producto p = productoSeleccionado();
        if (p != null) {
            mostrarTexto("Detalle del producto", p.getInfo(), 480, 260);
        }
    }

    private void verReporte() {
        mostrarTexto("Reporte de inventario", almacen.generarReporte(), 620, 520);
    }

    private void mostrarTexto(String titulo, String texto, int ancho, int alto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setBackground(Color.WHITE);
        area.setForeground(Tema.NEGRO);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new LineBorder(Tema.BORDE, 1));
        scroll.setPreferredSize(new Dimension(ancho, alto));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    private void exportar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("inventario_exportado.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            almacen.guardar(chooser.getSelectedFile().toPath());
            JOptionPane.showMessageDialog(this, "Inventario exportado correctamente.",
                    "Exportar", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardar() {
        try {
            almacen.guardar();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el inventario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean esNumerica(int columnaModelo) {
        return columnaModelo == 3 || columnaModelo == 4 || columnaModelo == 6;
    }

    private static class RenderizadorEncabezado extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionado,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
            setBackground(Tema.AZUL);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD));
            setBorder(new EmptyBorder(0, 10, 0, 10));
            setHorizontalAlignment(esNumerica(t.convertColumnIndexToModel(columna))
                    ? SwingConstants.RIGHT : SwingConstants.LEFT);
            return this;
        }
    }

    private class RenderizadorFilas extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionado,
                                                       boolean foco, int fila, int columna) {
            Object mostrar = valor;
            int columnaModelo = t.convertColumnIndexToModel(columna);
            if (valor instanceof Double) {
                mostrar = String.format("$%,.2f", (Double) valor);
            }
            Component c = super.getTableCellRendererComponent(t, mostrar, seleccionado, false, fila, columna);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            setHorizontalAlignment(esNumerica(columnaModelo) ? SwingConstants.RIGHT : SwingConstants.LEFT);
            if (seleccionado) {
                c.setBackground(Tema.TEAL);
                c.setForeground(Color.WHITE);
                return c;
            }
            String alerta = (String) t.getValueAt(fila, COL_ALERTA);
            if (alerta.contains("Sin stock") || alerta.contains("vencer")) {
                c.setBackground(Tema.FILA_ROJA);
            } else if (!alerta.isEmpty()) {
                c.setBackground(Tema.FILA_AMBAR);
            } else {
                c.setBackground(fila % 2 == 0 ? Color.WHITE : Tema.CLARO);
            }
            c.setForeground(Tema.NEGRO);
            return c;
        }
    }
}
