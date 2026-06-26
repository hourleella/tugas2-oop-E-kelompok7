package handler;

import server.Request;
import server.Response;
import server.Server;
import service.VenueService;
import model.Venue;

import java.util.List;
import java.util.Map;

public class VenueHandler {
    private final VenueService venueService;

    public VenueHandler(VenueService venueService) {
        this.venueService = venueService;
    }

    public void registerRoutes(Server server) {
        server.get("/api/venues", this::getAllVenues);
        server.get("/api/venues/{id}", this::getVenueById);
        server.post("/api/venues", this::createVenue);
        server.put("/api/venues/{id}", this::updateVenue);
    }

    private void getAllVenues(Request req, Response res) {
        try {
            List<Venue> venues = venueService.getAllVenues();
            res.sendSuccess(venues);
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void getVenueById(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            Map<String, Object> venueWithEvents = venueService.getVenueByIdWithEvents(id);
            res.sendSuccess(venueWithEvents);
        } catch (Exception e) {
            // Trik cerdas: Jika pesan error dari service mengandung "not found", return 404
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                res.sendError(404, e.getMessage());
            } else {
                res.sendError(500, "Internal Server Error: " + e.getMessage());
            }
        }
    }

    private void createVenue(Request req, Response res) {
        try {
            Map<String, Object> body = req.getJSON();
            Venue newVenue = new Venue();
            newVenue.setId("VNU-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            newVenue.setName((String) body.get("name"));
            newVenue.setAddress((String) body.get("address"));
            newVenue.setMaxCapacity((int) body.get("maxCapacity"));
            venueService.createVenue(newVenue);
            res.sendCreated(newVenue);
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }

    private void updateVenue(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            Map<String, Object> body = req.getJSON();
            Venue updatedVenue = new Venue();
            updatedVenue.setId(id);
            updatedVenue.setName((String) body.get("name"));
            updatedVenue.setAddress((String) body.get("address"));
            updatedVenue.setMaxCapacity((int) body.get("maxCapacity"));
            venueService.updateVenue(id, updatedVenue);
            res.sendSuccess(updatedVenue);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                res.sendError(404, e.getMessage());
            } else {
                res.sendError(400, "Bad Request: " + e.getMessage());
            }
        }
    }
}