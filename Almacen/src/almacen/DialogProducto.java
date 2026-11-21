package almacen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class DialogProducto extends JDialog {

    private static final String[] TIPOS = {"Perecedero", "Electrónico", "Materia Prima"};

    private final JComboBox<String> cmbTipo = new JComboBox<>(TIPOS);
    private final JTextField txtId = new JTextField(22);
    private final JTextField txtNombre = new JTextField(22);
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 0, 10000000, 1));
    private final JSpinner spnPrecio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100000000.0, 0.5));
    private final JSpinner spnDias = new JSpinner(new SpinnerNumberModel(5, 0, 365, 1));
    private final JSpinner spnTemperatura = new JSpinner(new SpinnerNumberModel(4.0, -50.0, 50.0, 0.5));
    private final JSpinner spnGarantia = new JSpinner(new SpinnerNumberModel(12, 0, 240, 1));
    private final JComboBox<String> cmbVoltaje = new JComboBox<>(new String[]{"110", "220"});
    private final JComboBox<String> cmbUnidad = new JComboBox<>(new String[]{"kg", "lt", "m", "unidad"});
    private final JTextField txtProveedor = new JTextField(22);
    private final CardLayout cartas = new CardLayout();
    private final JPanel panelExtra = new JPanel(cartas);
    private final Almacen almacen;
    private final boolean edicion;
    private Producto resultado;

    public DialogProducto(Frame dueno, Almacen almacen, Producto existente) {
        super(dueno, existente == null ? "Nuevo producto" : "Editar producto", true);
        this.almacen = almacen;
        this.edicion = existente != null;

        spnPrecio.setEditor(new JSpinner.NumberEditor(spnPrecio, "#,##0.00"));
        spnTemperatura.setEditor(new JSpinner.NumberEditor(spnTemperatura, "0.0"));
        cmbUnidad.setEditable(true);
        Tema.campo(txtId);
        Tema.campo(txtNombre);
        Tema.campo(txtProveedor);
        for (JComboBox<String> c : java.util.List.of(cmbTipo, cmbVoltaje, cmbUnidad)) {
            c.setBackground(Color.WHITE);
        }

        panelExtra.add(construirPanelPerecedero(), TIPOS[0]);
        panelExtra.add(construirPanelElectronico(), TIPOS[1]);
        panelExtra.add(construirPanelMateria(), TIPOS[2]);

        JPanel comunes = new JPanel(new GridBagLayout());
        fila(comunes, 0, "Tipo", cmbTipo);
        fila(comunes, 1, "ID", txtId);
        fila(comunes, 2, "Nombre", txtNombre);
        fila(comunes, 3, "Cantidad", spnCantidad);
        fila(comunes, 4, "Precio unitario ($)", spnPrecio);

        TitledBorder borde = BorderFactory.createTitledBorder(new LineBorder(Tema.BORDE, 1), " Datos específicos ");
        borde.setTitleColor(Tema.AZUL);
        borde.setTitleFont(borde.getTitleFont().deriveFont(Font.BOLD));
        JPanel contenedorExtra = new JPanel(new BorderLayout());
        contenedorExtra.setBorder(new CompoundBorder(borde, new EmptyBorder(4, 6, 6, 6)));
        contenedorExtra.add(panelExtra, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBorder(new EmptyBorder(16, 18, 6, 18));
        centro.add(comunes, BorderLayout.NORTH);
        centro.add(contenedorExtra, BorderLayout.CENTER);

        JLabel titulo = new JLabel(edicion ? "Editar producto" : "Nuevo producto");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Tema.NEGRO);
        cabecera.setBorder(new CompoundBorder(new MatteBorder(0, 0, 3, 0, Tema.AMBAR),
                new EmptyBorder(12, 18, 10, 18)));
        cabecera.add(titulo, BorderLayout.WEST);

        JButton btnAceptar = Tema.boton("Guardar", Tema.AMBAR, Tema.NEGRO);
        JButton btnCancelar = Tema.botonContorno("Cancelar");
        btnAceptar.addActionListener(e -> aceptar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        botones.setBorder(new EmptyBorder(0, 10, 4, 10));
        botones.add(btnCancelar);
        botones.add(btnAceptar);

        getContentPane().setBackground(Tema.CLARO);
        setLayout(new BorderLayout());
        add(cabecera, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnAceptar);

        cmbTipo.addActionListener(e -> cartas.show(panelExtra, (String) cmbTipo.getSelectedItem()));

        if (edicion) {
            cargar(existente);
        }

        pack();
        setResizable(false);
        setLocationRelativeTo(dueno);
    }

    public Producto getResultado() {
        return resultado;
    }

    private JPanel construirPanelPerecedero() {
        JPanel p = new JPanel(new GridBagLayout());
        fila(p, 0, "Días de vigencia", spnDias);
        fila(p, 1, "Temperatura (°C)", spnTemperatura);
        return p;
    }

    private JPanel construirPanelElectronico() {
        JPanel p = new JPanel(new GridBagLayout());
        fila(p, 0, "Garantía (meses)", spnGarantia);
        fila(p, 1, "Voltaje (V)", cmbVoltaje);
        return p;
    }

    private JPanel construirPanelMateria() {
        JPanel p = new JPanel(new GridBagLayout());
        fila(p, 0, "Unidad de medida", cmbUnidad);
        fila(p, 1, "Proveedor", txtProveedor);
        return p;
    }

    private void fila(JPanel panel, int y, String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 4, 5, 8);
        c.gridy = y;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        JLabel l = new JLabel(etiqueta);
        l.setForeground(Tema.AZUL);
        panel.add(l, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 4, 5, 4);
        panel.add(campo, c);
    }

    private void cargar(Producto p) {
        cmbTipo.setSelectedItem(p.getTipo());
        cmbTipo.setEnabled(false);
        txtId.setText(p.getId());
        txtId.setEnabled(false);
        txtNombre.setText(p.getNombre());
        spnCantidad.setValue(p.getCantidad());
        spnPrecio.setValue(p.getPrecio());
        if (p instanceof ProductoPerecedero) {
            ProductoPerecedero pp = (ProductoPerecedero) p;
            spnDias.setValue(pp.getDiasVigencia());
            spnTemperatura.setValue(pp.getTemperatura());
        } else if (p instanceof ProductoElectronico) {
            ProductoElectronico pe = (ProductoElectronico) p;
            spnGarantia.setValue(pe.getGarantiaMeses());
            cmbVoltaje.setSelectedItem(String.valueOf(pe.getVoltaje()));
        } else if (p instanceof MateriaPrima) {
            MateriaPrima mp = (MateriaPrima) p;
            cmbUnidad.setSelectedItem(mp.getUnidadMedida());
            txtProveedor.setText(mp.getProveedor());
        }
        cartas.show(panelExtra, p.getTipo());
    }

    private void aceptar() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (id.isEmpty() || nombre.isEmpty()) {
            advertir("El ID y el nombre son obligatorios.");
            return;
        }
        if (!edicion && almacen.existeId(id)) {
            advertir("Ya existe un producto con el ID \"" + id + "\".");
            return;
        }
        int cantidad = ((Number) spnCantidad.getValue()).intValue();
        double precio = ((Number) spnPrecio.getValue()).doubleValue();

        switch (cmbTipo.getSelectedIndex()) {
            case 0:
                resultado = new ProductoPerecedero(id, nombre, cantidad, precio,
                        ((Number) spnDias.getValue()).intValue(),
                        ((Number) spnTemperatura.getValue()).doubleValue());
                break;
            case 1:
                resultado = new ProductoElectronico(id, nombre, cantidad, precio,
                        ((Number) spnGarantia.getValue()).intValue(),
                        Integer.parseInt((String) cmbVoltaje.getSelectedItem()));
                break;
            default:
                String unidad = String.valueOf(cmbUnidad.getSelectedItem()).trim();
                String proveedor = txtProveedor.getText().trim();
                resultado = new MateriaPrima(id, nombre, cantidad, precio,
                        unidad.isEmpty() ? "unidad" : unidad,
                        proveedor.isEmpty() ? "N/D" : proveedor);
        }
        dispose();
    }

    private void advertir(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos incompletos", JOptionPane.WARNING_MESSAGE);
    }
}
