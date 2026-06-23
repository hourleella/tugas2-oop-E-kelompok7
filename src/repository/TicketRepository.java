package repository;

import database.DatabaseManager;
import model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository {

    // Save tiket baru
    public void save(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (id, event_id, user_id, category, quantity, unit_price, total_price, status, refund_amount) VALUES (?, ?, ?, ?, ?, ?, ?, 'active', 0)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getId());
            ps.setString(2, ticket.getEventId());
            ps.setString(3, ticket.getUserId());
            ps.setString(4, ticket.getCategory());
            ps.setInt(5, ticket.getQuantity());
            ps.setDouble(6, ticket.getUnitPrice());
            ps.setDouble(7, ticket.getTotalPrice());
            ps.executeUpdate();
        }
    }

    // Cari tiket dengan ID
    public Ticket findById(String id) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToTicket(rs);
            }
            return null;
        }
    }

    // Get semua tiket milik satu user
    public List<Ticket> findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            List<Ticket> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRowToTicket(rs));
            }
            return list;
        }
    }

    // Get semua tiket untuk satu event
    public List<Ticket> findByEventId(String eventId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE event_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();

            List<Ticket> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRowToTicket(rs));
            }
            return list;
        }
    }

    // Update status tiket jadi refunded
    public void updateRefund(String ticketId, double refundAmount) throws SQLException {
        String sql = "UPDATE tickets SET status = 'refunded', refund_amount = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, refundAmount);
            ps.setString(2, ticketId);
            ps.executeUpdate();
        }
    }

    // Helper: ubah baris database menjadi objek Ticket
    private Ticket mapRowToTicket(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getString("id"),
                rs.getString("event_id"),
                rs.getString("user_id"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("unit_price"),
                rs.getDouble("total_price"),
                rs.getString("status"),
                rs.getDouble("refund_amount")
        );
    }
}