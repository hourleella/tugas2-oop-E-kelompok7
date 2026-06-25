package handler;

import server.Request;
import server.Response;
import server.Server;
import service.EventService;
import exception.EventNotFoundException;

import java.util.Map;

public class EventHandler {
    private final EventService eventService;

    public EventHandler(EventService eventservice) {
        this.eventService = eventService;
    }
    public void registerRoutes(Server server) {
        server.get("/api/events/price-summary", this::getPriceSummary);
        server.get("/api/events", this::getAllEvent);
        server.get("/api/events/{id}", this::getEventById);
        server.post("/api/events", this::createEvent);
        server.put("/api/events/{id}", this::updateEvent);
    }

    private void getAllEvents(Request req, Response res) {
        try {
            String type = req.getQueryParam("type");
            String dateFrom = req.getQueryParam("dateFrom");
            res.sendSuccess(eventService.getAllEvents(type, dateFrom));
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void getPriceSummary(Request req, Response res) {
        try {
            res.sendSuccess(eventService.getPriceSummaryReport());
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void getEventById(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            res.sendSuccess(eventService.getEventDetailById(id));
        } catch (EventNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void createEvent(Request req, Response res) {
        try {
            Map<String, Object> body = req.getJSON();
            res.sendCreated(eventService.createEvent(body));
        } catch (IllegalArgumentException e) {
            res.sendError(400, "Validation Failed: " + e.getMessage());
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void updateEvent(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            Map<String, Object> body = req.getJSON();
            res.sendSuccess(eventService.updateEvent(id, body));
        } catch (EventNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }
}