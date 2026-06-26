package handler;

import server.Request;
import server.Response;
import server.Server;
import service.UserService;
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
        } catch (IllegalArgumentException e) { // Spesifik ditangkap lebih dulu
            res.sendError(404, e.getMessage());
        } catch (Exception e) { // Umum ditangkap terakhir
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void createUser(Request req, Response res) {
        try {
            Map<String, Object> body = req.getJSON();
            User newUser = new User();
            newUser.setId("USR-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            newUser.setName((String) body.get("name"));
            newUser.setEmail((String) body.get("email"));
            newUser.setPhone((String) body.get("phone"));
            newUser.setRole((String) body.get("role"));
            userService.registerUser(newUser);
            res.sendCreated(newUser);
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }

    private void updateUser(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            Map<String, Object> body = req.getJSON();
            User updatedUser = new User();
            updatedUser.setId(id);
            updatedUser.setName((String) body.get("name"));
            updatedUser.setEmail((String) body.get("email"));
            updatedUser.setPhone((String) body.get("phone"));
            updatedUser.setRole((String) body.get("role"));
            userService.updateUser(id, updatedUser);
            res.sendSuccess(updatedUser);
        } catch (Exception e) {
           if(e.getMessage() != null && e.getMessage().contains("not found")) {
                res.sendError(404, "Not Found: " + e.getMessage());
            } else {
                res.sendError(400, "Bad Request: " + e.getMessage());
            }
        }
    }
}