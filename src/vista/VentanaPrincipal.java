package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    // Instancias de los Data Access Objects (DAO)
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    // Componentes: Pestaña Repartidores
    private JTextField txtRepartidorNombre;
    private JTable tablaRepartidores;
    private DefaultTableModel modeloRepartidores;
    private int idRepartidorSeleccionado = -1;

    // Componentes: Pestaña Pedidos
    private JTextField txtPedidoDireccion;
    private JComboBox<String> cbPedidoTipo;
    private JComboBox<String> cbPedidoEstado;
    private JTable tablaPedidos;
    private DefaultTableModel modeloPedidos;
    private int idPedidoSeleccionado = -1;

    // Componentes: Pestaña Entregas
    private JComboBox<Pedido> cbEntregaPedidos;
    private JComboBox<Repartidor> cbEntregaRepartidores;
    private JTextField txtEntregaFecha;
    private JTextField txtEntregaHora;
    private JTable tablaEntregas;
    private DefaultTableModel modeloEntregas;
    private int idEntregaSeleccionada = -1;

    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión Integral de Operaciones");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Contenedor principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Repartidores", crearPanelRepartidores());
        tabbedPane.addTab("Pedidos", crearPanelPedidos());
        tabbedPane.addTab("Entregas", crearPanelEntregas());

        add(tabbedPane);

        // Cargar los datos iniciales desde la base de datos
        recargarTodo();
    }

    // ==========================================
    // 1. MÓDULO REPARTIDORES
    // ==========================================
    private JPanel crearPanelRepartidores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario de datos
        JPanel form = new JPanel(new GridLayout(1, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Repartidor"));
        form.add(new JLabel("Nombre Completo:"));
        txtRepartidorNombre = new JTextField();
        form.add(txtRepartidorNombre);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar Formulario");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(form, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de repartidores
        modeloRepartidores = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRepartidores = new JTable(modeloRepartidores);
        panel.add(new JScrollPane(tablaRepartidores), BorderLayout.CENTER);

        // Evento al seleccionar fila
        tablaRepartidores.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaRepartidores.getSelectedRow();
            if (fila >= 0) {
                idRepartidorSeleccionado = (int) modeloRepartidores.getValueAt(fila, 0);
                txtRepartidorNombre.setText((String) modeloRepartidores.getValueAt(fila, 1));
            }
        });

        // Eventos de botones
        btnRegistrar.addActionListener(e -> {
            String nombre = txtRepartidorNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del repartidor es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                repartidorDAO.create(new Repartidor(nombre));
                JOptionPane.showMessageDialog(this, "Repartidor registrado correctamente.");
                recargarTodo();
                limpiarFormRepartidor();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e -> {
            if (idRepartidorSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un repartidor de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nombre = txtRepartidorNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del repartidor es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                repartidorDAO.update(new Repartidor(idRepartidorSeleccionado, nombre));
                JOptionPane.showMessageDialog(this, "Repartidor actualizado correctamente.");
                recargarTodo();
                limpiarFormRepartidor();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            if (idRepartidorSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un repartidor para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar al repartidor seleccionado? Se eliminarán también sus entregas asociadas.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    repartidorDAO.delete(idRepartidorSeleccionado);
                    JOptionPane.showMessageDialog(this, "Repartidor eliminado.");
                    recargarTodo();
                    limpiarFormRepartidor();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarFormRepartidor());

        return panel;
    }

    private void limpiarFormRepartidor() {
        txtRepartidorNombre.setText("");
        idRepartidorSeleccionado = -1;
        tablaRepartidores.clearSelection();
    }

    // ==========================================
    // 2. MÓDULO PEDIDOS
    // ==========================================
    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario de datos
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Pedido"));

        form.add(new JLabel("Dirección de Entrega:"));
        txtPedidoDireccion = new JTextField();
        form.add(txtPedidoDireccion);

        form.add(new JLabel("Tipo de Pedido:"));
        cbPedidoTipo = new JComboBox<>(new String[]{"COMIDA", "ENCOMIENDA", "EXPRESS"});
        form.add(cbPedidoTipo);

        form.add(new JLabel("Estado:"));
        cbPedidoEstado = new JComboBox<>(new String[]{"PENDIENTE", "EN_REPARTO", "ENTREGADO"});
        form.add(cbPedidoEstado);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar Formulario");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(form, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de pedidos
        modeloPedidos = new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPedidos = new JTable(modeloPedidos);
        panel.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        // Evento al seleccionar fila
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPedidos.getSelectedRow();
            if (fila >= 0) {
                idPedidoSeleccionado = (int) modeloPedidos.getValueAt(fila, 0);
                txtPedidoDireccion.setText((String) modeloPedidos.getValueAt(fila, 1));
                cbPedidoTipo.setSelectedItem(modeloPedidos.getValueAt(fila, 2));
                cbPedidoEstado.setSelectedItem(modeloPedidos.getValueAt(fila, 3));
            }
        });

        // Eventos de botones
        btnRegistrar.addActionListener(e -> {
            String direccion = txtPedidoDireccion.getText().trim();
            if (direccion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La dirección del pedido es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                pedidoDAO.create(new Pedido(
                        direccion,
                        (String) cbPedidoTipo.getSelectedItem(),
                        (String) cbPedidoEstado.getSelectedItem()
                ));
                JOptionPane.showMessageDialog(this, "Pedido registrado con éxito.");
                recargarTodo();
                limpiarFormPedido();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e -> {
            if (idPedidoSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String direccion = txtPedidoDireccion.getText().trim();
            if (direccion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La dirección del pedido es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                pedidoDAO.update(new Pedido(
                        idPedidoSeleccionado,
                        direccion,
                        (String) cbPedidoTipo.getSelectedItem(),
                        (String) cbPedidoEstado.getSelectedItem()
                ));
                JOptionPane.showMessageDialog(this, "Pedido actualizado con éxito.");
                recargarTodo();
                limpiarFormPedido();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            if (idPedidoSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un pedido para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar el pedido? Se eliminarán también las entregas asociadas.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    pedidoDAO.delete(idPedidoSeleccionado);
                    JOptionPane.showMessageDialog(this, "Pedido eliminado.");
                    recargarTodo();
                    limpiarFormPedido();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarFormPedido());

        return panel;
    }

    private void limpiarFormPedido() {
        txtPedidoDireccion.setText("");
        cbPedidoTipo.setSelectedIndex(0);
        cbPedidoEstado.setSelectedIndex(0);
        idPedidoSeleccionado = -1;
        tablaPedidos.clearSelection();
    }

    // ==========================================
    // 3. MÓDULO ENTREGAS
    // ==========================================
    private JPanel crearPanelEntregas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario de datos
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Asignación de Entregas"));

        form.add(new JLabel("Pedido (ID - Dirección):"));
        cbEntregaPedidos = new JComboBox<>();
        form.add(cbEntregaPedidos);

        form.add(new JLabel("Repartidor Asignado:"));
        cbEntregaRepartidores = new JComboBox<>();
        form.add(cbEntregaRepartidores);

        form.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtEntregaFecha = new JTextField(LocalDate.now().toString());
        form.add(txtEntregaFecha);

        form.add(new JLabel("Hora (HH:MM:SS):"));
        txtEntregaHora = new JTextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        form.add(txtEntregaHora);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegistrar = new JButton("Registrar Entrega");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar Formulario");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(form, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de entregas
        modeloEntregas = new DefaultTableModel(new String[]{"ID Entrega", "ID Pedido", "ID Repartidor", "Fecha", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEntregas = new JTable(modeloEntregas);
        panel.add(new JScrollPane(tablaEntregas), BorderLayout.CENTER);

        // Evento al seleccionar fila
        tablaEntregas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEntregas.getSelectedRow();
            if (fila >= 0) {
                idEntregaSeleccionada = (int) modeloEntregas.getValueAt(fila, 0);
                int idPedidoTabla = (int) modeloEntregas.getValueAt(fila, 1);
                int idRepartidorTabla = (int) modeloEntregas.getValueAt(fila, 2);

                txtEntregaFecha.setText(modeloEntregas.getValueAt(fila, 3).toString());
                txtEntregaHora.setText(modeloEntregas.getValueAt(fila, 4).toString());

                // Posicionar los JComboBox en los elementos seleccionados
                for (int i = 0; i < cbEntregaPedidos.getItemCount(); i++) {
                    if (cbEntregaPedidos.getItemAt(i).getId() == idPedidoTabla) {
                        cbEntregaPedidos.setSelectedIndex(i);
                        break;
                    }
                }

                for (int i = 0; i < cbEntregaRepartidores.getItemCount(); i++) {
                    if (cbEntregaRepartidores.getItemAt(i).getId() == idRepartidorTabla) {
                        cbEntregaRepartidores.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        // Eventos de botones
        btnRegistrar.addActionListener(e -> {
            Pedido pedido = (Pedido) cbEntregaPedidos.getSelectedItem();
            Repartidor repartidor = (Repartidor) cbEntregaRepartidores.getSelectedItem();

            if (pedido == null || repartidor == null) {
                JOptionPane.showMessageDialog(this, "Debe existir al menos un Pedido y un Repartidor registrado.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Date fecha = Date.valueOf(txtEntregaFecha.getText().trim());
                Time hora = Time.valueOf(txtEntregaHora.getText().trim());

                entregaDAO.create(new Entrega(pedido.getId(), repartidor.getId(), fecha, hora));
                JOptionPane.showMessageDialog(this, "Entrega registrada con éxito.");
                recargarTodo();
                limpiarFormEntrega();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha u hora incorrecto.\nFecha: YYYY-MM-DD (ej: 2026-10-03)\nHora: HH:MM:SS (ej: 14:30:00)", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e -> {
            if (idEntregaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una entrega de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pedido pedido = (Pedido) cbEntregaPedidos.getSelectedItem();
            Repartidor repartidor = (Repartidor) cbEntregaRepartidores.getSelectedItem();

            try {
                Date fecha = Date.valueOf(txtEntregaFecha.getText().trim());
                Time hora = Time.valueOf(txtEntregaHora.getText().trim());

                entregaDAO.update(new Entrega(idEntregaSeleccionada, pedido.getId(), repartidor.getId(), fecha, hora));
                JOptionPane.showMessageDialog(this, "Entrega actualizada con éxito.");
                recargarTodo();
                limpiarFormEntrega();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha u hora incorrecto.\nFecha: YYYY-MM-DD\nHora: HH:MM:SS", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            if (idEntregaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una entrega para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar el registro de esta entrega?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    entregaDAO.delete(idEntregaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Entrega eliminada.");
                    recargarTodo();
                    limpiarFormEntrega();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarFormEntrega());

        return panel;
    }

    private void limpiarFormEntrega() {
        idEntregaSeleccionada = -1;
        tablaEntregas.clearSelection();
        txtEntregaFecha.setText(LocalDate.now().toString());
        txtEntregaHora.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ==========================================
    // 4. SINCRONIZACIÓN GENERAL DE DATOS
    // ==========================================
    private void recargarTodo() {
        try {
            // Sincronizar Repartidores
            modeloRepartidores.setRowCount(0);
            cbEntregaRepartidores.removeAllItems();
            List<Repartidor> listaRepartidores = repartidorDAO.readAll();
            for (Repartidor r : listaRepartidores) {
                modeloRepartidores.addRow(new Object[]{r.getId(), r.getNombre()});
                cbEntregaRepartidores.addItem(r);
            }

            // Sincronizar Pedidos
            modeloPedidos.setRowCount(0);
            cbEntregaPedidos.removeAllItems();
            List<Pedido> listaPedidos = pedidoDAO.readAll();
            for (Pedido p : listaPedidos) {
                modeloPedidos.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo(), p.getEstado()});
                cbEntregaPedidos.addItem(p);
            }

            // Sincronizar Entregas
            modeloEntregas.setRowCount(0);
            List<Entrega> listaEntregas = entregaDAO.readAll();
            for (Entrega ent : listaEntregas) {
                modeloEntregas.addRow(new Object[]{
                        ent.getId(),
                        ent.getIdPedido(),
                        ent.getIdRepartidor(),
                        ent.getFecha(),
                        ent.getHora()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al sincronizar con la base de datos: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // MÉTODO PRINCIPAL DE ENTRADA
    // ==========================================
    public static void main(String[] args) {
        // Establecer apariencia nativa del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}