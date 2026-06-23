package repository;

import database.DatabaseManager;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    // Cari event dengan ID
    public Event findById(String id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToEvent(rs, conn);
            }
            return null;
        }
    }

    // Get semua events dengan filter opsional
    public List<Event> findAll(String type, String dateFrom) throws SQLException {
        String sql = "SELECT * FROM events WHERE 1=1";
        if (type != null) sql += " AND type = ?";
        if (dateFrom != null) sql += " AND date >= ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            if (type != null) ps.setString(i++, type);
            if (dateFrom != null) ps.setString(i++, dateFrom);

            ResultSet rs = ps.executeQuery();
            List<Event> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRowToEvent(rs, conn));
            }
            return list;
        }
    }

    // Save event baru
    public void save(Event event) throws SQLException {
        String sql = "INSERT INTO events (id, type, name, venue_id, organizer_id, date, base_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getId());
            ps.setString(2, event.getType());
            ps.setString(3, event.getName());
            ps.setString(4, event.getVenueId());
            ps.setString(5, event.getOrganizerId());
            ps.setString(6, event.getDate());
            ps.setDouble(7, event.getBasePrice());
            ps.executeUpdate();
        }

        // Save kapasitas event
        saveCapacities(event);
    }

    // Update event
    public void update(Event event) throws SQLException {
        String sql = "UPDATE events SET name = ?, date = ?, base_price = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getName());
            ps.setString(2, event.getDate());
            ps.setDouble(3, event.getBasePrice());
            ps.setString(4, event.getId());
            ps.executeUpdate();
        }
    }

    // Cek apa venue udah dibook pada tanggal
    public boolean venueIsBooked(String venueId, String date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM events WHERE venue_id = ? AND date = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venueId);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Save baris kapasitas event
    private void saveCapacities(Event event) throws SQLException {
        String sql = "INSERT INTO capacities (event_id, category, total, filled) VALUES (?, ?, ?, 0)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (var entry : event.getCapacities().entrySet()) {
                ps.setString(1, event.getId());
                ps.setString(2, entry.getKey());
                ps.setInt(3, entry.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Get sisa kapasitas per kategori untuk event
    public java.util.Map<String, Integer> getRemainingCapacity(String eventId) throws SQLException {
        String sql = "SELECT category, total - filled AS remaining FROM capacities WHERE event_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            java.util.Map<String, Integer> map = new java.util.HashMap<>();
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getInt("remaining"));
            }
            return map;
        }
    }

    // Kurangi jumlah filled saat tiket dibeli
    public void fillCapacity(String eventId, String category, int quantity) throws SQLException {
        String sql = "UPDATE capacities SET filled = filled + ? WHERE event_id = ? AND category = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, eventId);
            ps.setString(3, category);
            ps.executeUpdate();
        }
    }

    // Helper: ubah baris database menjadi subclass yang sesuai
    private Event mapRowToEvent(ResultSet rs, Connection conn) throws SQLException {
        String type = rs.getString("type");
        String id = rs.getString("id");
        String name = rs.getString("name");
        String venueId = rs.getString("venue_id");
        String organizerId = rs.getString("organizer_id");
        String date = rs.getString("date");
        double basePrice = rs.getDouble("base_price");

        // load kapasitas event
        java.util.Map<String, Integer> capacities = loadCapacities(id, conn);

        // instantiate subclass sesuai dengan type
        switch (type) {
            case "concert": return new Concert(id, name, venueId, organizerId, date, basePrice, capacities);
            case "seminar": return new Seminar(id, name, venueId, organizerId, date, basePrice, capacities);
            case "sport_match": return new SportMatch(id, name, venueId, organizerId, date, basePrice, capacities);
            default: throw new SQLException("Unknown event type: " + type);
        }
    }

    private java.util.Map<String, Integer> loadCapacities(String eventId, Connection conn) throws SQLException {
        String sql = "SELECT category, total FROM capacities WHERE event_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, eventId);
        ResultSet rs = ps.executeQuery();
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        while (rs.next()) {
            map.put(rs.getString("category"), rs.getInt("total"));
        }
        return map;
    }
}