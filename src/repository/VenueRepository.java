package repository;

import database.DatabaseManager;
import model.Venue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueRepository {

    // Simpan venue baru
    public void save(Venue venue) throws SQLException {
        String sql = "INSERT INTO venues (id, name, address, max_capacity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venue.getId());
            ps.setString(2, venue.getName());
            ps.setString(3, venue.getAddress());
            ps.setInt(4, venue.getMaxCapacity());
            ps.executeUpdate();
        }
    }

    // Cari venue berdasarkan ID
    public Venue findById(String id) throws SQLException {
        String sql = "SELECT * FROM venues WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToVenue(rs);
            }
            return null;
        }
    }

    // Ambil semua venue
    public List<Venue> findAll() throws SQLException {
        String sql = "SELECT * FROM venues";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            List<Venue> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRowToVenue(rs));
            }
            return list;
        }
    }

    // Update data venue
    public void update(Venue venue) throws SQLException {
        String sql = "UPDATE venues SET name = ?, address = ?, max_capacity = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venue.getName());
            ps.setString(2, venue.getAddress());
            ps.setInt(3, venue.getMaxCapacity());
            ps.setString(4, venue.getId());
            ps.executeUpdate();
        }
    }

    // Ambil semua event di sebuah venue
    public List<String[]> getEventsByVenueId(String venueId) throws SQLException {
        String sql = "SELECT id, name, date FROM events WHERE venue_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venueId);
            ResultSet rs = ps.executeQuery();

            List<String[]> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("date")
                });
            }
            return list;
        }
    }

    // Helper: ubah baris database menjadi objek Venue
    private Venue mapRowToVenue(ResultSet rs) throws SQLException {
        return new Venue(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getInt("max_capacity"),
                rs.getString("created_at")
        );
    }
}