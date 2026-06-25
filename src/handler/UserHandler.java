package handler;

import server.Request;
import server.Response;
import server.Server;
import service.UserService;
import exception.UserNotFoundException;
import model.User;

import java.util.List;
import java.util.Map;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void registerRoutes(Server server) {
        server.get("/api/users", this::getAllUsers);
        server.get("/api/users/{id}", this::getUserById);
        server.post("/api/users", this::createUser);
        server.put("/api/users/{id}", this::updateUser);
    }

    private void getAllUsers(Request req, Response res) {
        try {
            String role = req.getQueryParam("role");
            List<User> users = userService.getAllUsers(role);
            res.sendSuccess(users);
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void getUserById(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            Map<String, Object> userWithSummary = userService.getUserByIdWithSummary(id);
            res.sendSuccess(userWithSummary);
        } catch (UserNotFoundException e) { // Spesifik ditangkap lebih dulu
            res.sendError(404, e.getMessage());
        } catch (Exception e) { // Umum ditangkap terakhir
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void createUser(Request req, Response res) {
        try {
            Map<String, Object> body = req.getJSON();
            User newUser = userService.createUser(body);
            res.sendCreated(newUser);
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }

    private void updateUser(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            Map<String, Object> body = req.getJSON();
            User updatedUser = userService.updateUser(id, body);
            res.sendSuccess(updatedUser);
        } catch (UserNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }
}