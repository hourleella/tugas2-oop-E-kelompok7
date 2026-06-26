package repository;

import database.DatabaseManager;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // Save user baru
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, phone, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());
            ps.executeUpdate();
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
            return null;
        }
    }

    // Cari user dengan ID
    public User findById(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
            return null;
        }
    }

    // Ambil semua user, dengan filter role opsional
    public List<User> findAll(String role) throws SQLException {
        String sql = "SELECT * FROM users";
        if (role != null) sql += " WHERE role = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (role != null) ps.setString(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                List<User> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
                return list;
            }
        }
    }

    // Update data user
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getId());
            ps.executeUpdate();
        }
    }

    // Cek apakah email sudah digunakan
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Ambil ringkasan aktivitas pembeli (total tiket dan total spending)
    public int[] getBuyerSummary(String userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantity), 0), COALESCE(SUM(total_price), 0) FROM tickets WHERE user_id = ? AND status = 'active'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt(1), rs.getInt(2)};
                }
                return new int[]{0, 0};
            }
        }
    }

    // Ambil ringkasan aktivitas organizer (total event dan total revenue)
    public int[] getOrganizerSummary(String userId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT e.id), COALESCE(SUM(t.total_price), 0) FROM events e LEFT JOIN tickets t ON e.id = t.event_id WHERE e.organizer_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt(1), rs.getInt(2)};
                }
                return new int[]{0, 0};
            }
        }
    }

    // Helper: ubah baris database menjadi objek User
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getString("created_at"));
        return user;
    }
}