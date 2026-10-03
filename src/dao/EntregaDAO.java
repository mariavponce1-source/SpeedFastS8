package dao;

import conexion.ConexionDB;
import modelo.Entrega;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    public boolean create(Entrega e) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getIdPedido());
            ps.setInt(2, e.getIdRepartidor());
            ps.setDate(3, e.getFecha());
            ps.setTime(4, e.getHora());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Entrega> readAll() throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT id, id_pedido, id_repartidor, fecha, hora FROM entregas ORDER BY id ASC";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Entrega(
                        rs.getInt("id"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_repartidor"),
                        rs.getDate("fecha"),
                        rs.getTime("hora")
                ));
            }
        }
        return lista;
    }

    public boolean update(Entrega e) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getIdPedido());
            ps.setInt(2, e.getIdRepartidor());
            ps.setDate(3, e.getFecha());
            ps.setTime(4, e.getHora());
            ps.setInt(5, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}