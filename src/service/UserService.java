package service;

import model.User;
import repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        try{
            if (user.getName() == null || user.getEmail() == null || user.getPhone() == null) {
                throw new IllegalArgumentException("Nama, email, dan telepon harus diisi.");
            }
            if (userRepository.emailExists(user.getEmail())) {
                throw new IllegalArgumentException("Error: User dengan email tersebut sudah terdaftar.");
            }
            if (user.getRole() == null || (!user.getRole().equals("buyer") && !user.getRole().equals("organizer"))) {
                throw new IllegalArgumentException("Error : Role tidak valid");
            }

            userRepository.save(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while registering user:" + e.getMessage(), e);
        }
    }

    public Map<String, Object> getUserByIdWithSummary(String id) {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                throw new IllegalArgumentException("Error : User dengan ID " + id + " tidak ditemukan");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("name", user.getName());
            result.put("email", user.getEmail());
            result.put("phone", user.getPhone());
            result.put("role", user.getRole());

            if ("organizer".equals(user.getRole())) {
                int[] summary = userRepository.getOrganizerSummary(id);
                Map<String, Object> summaryMap = new HashMap<>();
                summaryMap.put("totalEventsCreated", summary[0]);
                summaryMap.put("totalRevenue", summary[1]);
                result.put("summary", summaryMap);
            } else {
                int[] summary = userRepository.getBuyerSummary(id);
                Map<String, Object> summaryMap = new HashMap<>();
                summaryMap.put("totalTicketsPurchased", summary[0]);
                summaryMap.put("totalSpending", summary[1]);
                result.put("summary", summaryMap);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat mengambil user -" + e.getMessage(), e);
        }
    }

    public void updateUser(String id, User updatedUser) {
        try {
            User existingUser = userRepository.findById(id);
            if (existingUser == null) {
                throw new IllegalArgumentException("Error : User dengan ID tersebut tidak ditemukan.");
            }
            userRepository.update(updatedUser);
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat update user -" + e.getMessage(), e);
        }
    }
}