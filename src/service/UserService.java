package service;

import model.User;
import repository.UserRepository;

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
            User existingUser = userRepository.findById(user.getId());
            if (existingUser != null) {
                throw new IllegalArgumentException("Error: User dengan ID tersebut sudah terdaftar.");
            }

            if (user.getRole() == null || (!user.getRole().equals("customer") && !user.getRole().equals("organizer"))) {
                throw new IllegalArgumentException("Error : Role tidak valid");
            }

            userRepository.save(user);

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while registering user.", e);
        }
    }

    public User login(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null || !user.getPassword().equals(password)) {
                throw new IllegalArgumentException("Error: Email atau password salah.");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat login -" + e.getMessage(), e);
        }
    }

    public User getUserById(String id) {
        try {
            return userRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat mengambil user -" + e.getMessage(), e);
        }
    }
}
