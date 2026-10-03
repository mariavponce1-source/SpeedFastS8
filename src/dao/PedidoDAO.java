package dao;

import conexion.ConexionDB;
import modelo.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public boolean create(Pedido p) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getDireccion());
            ps.setString(2, p.getTipo());
            ps.setString(3, p.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Pedido> readAll() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, direccion, tipo, estado FROM pedidos ORDER BY id ASC";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Pedido(
                        rs.getInt("id"),
                        rs.getString("direccion"),
                        rs.getString("tipo"),
                        rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    public boolean update(Pedido p) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getDireccion());
            ps.setString(2, p.getTipo());
            ps.setString(3, p.getEstado());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}