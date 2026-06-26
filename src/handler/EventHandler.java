package handler;

import server.Request;
import server.Response;
import server.Server;
import service.EventService;
import model.Event;
import exception.EventNotFoundException;

import java.util.Map;

public class EventHandler {
    private final EventService eventService;

    public EventHandler(EventService eventService) {
        this.eventService = eventService;
    }
    public void registerRoutes(Server server) {
        server.get("/api/events/price-summary", this::getPriceSummary);
        server.get("/api/events", this::getAllEvents);
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
            res.sendSuccess(eventService.getEventById(id));
        } catch (EventNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void createEvent(Request req, Response res) {
        try {
            Map<String, Object> body = req.getJSON();
            Event event = new Event();
            event.setName((String) body.get("name"));
            event.setType((String) body.get("type"));
            event.setDate((String) body.get("date"));
            event.setVenueId((String) body.get("venueId"));
            event.setOrganizerId((String) body.get("organizerId"));

            Map<String, Integer> capacities = (Map<String, Integer>) body.get("capacities");

            eventService.createEvent(event, capacities);
            res.sendCreated(event);
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

            Event updatedEvent = new Event();
            updatedEvent.setId(id);
            updatedEvent.setName((String) body.get("name"));
            updatedEvent.setType((String) body.get("type"));
            updatedEvent.setDate((String) body.get("date"));
            eventService.updateEvent(id, updatedEvent);
            res.sendSuccess("Event updated successfully.");
        } catch (EventNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }
}