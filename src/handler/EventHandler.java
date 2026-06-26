package handler;

import server.Request;
import server.Response;
import server.Server;
import service.EventService;
import model.Event;
import model.Concert;
import model.Seminar;
import model.SportMatch;
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
            String type = (String) body.get("type");
            String id = "EVT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String name = (String) body.get("name");
            String venueId = (String) body.get("venueId");
            String organizerId = (String) body.get("organizerId");
            String date = (String) body.get("date");
            double basePrice = ((Number) body.get("basePrice")).doubleValue();
            Map<String, Integer> capacities = (Map<String, Integer>) body.get("capacities");

            Event event;
            switch (type) {
                case "concert": event = new Concert(id, name, venueId, organizerId, date, basePrice, capacities); break;
                case "seminar": event = new Seminar(id, name, venueId, organizerId, date, basePrice, capacities); break;
                case "sport_match": event = new SportMatch(id, name, venueId, organizerId, date, basePrice, capacities); break;
                default: res.sendError(400, "Tipe event tidak valid"); return;
            }

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
            Event existing = eventService.getEventById(id);
            existing.setName((String) body.get("name"));
            existing.setDate((String) body.get("date"));
            existing.setBasePrice(((Number) body.get("basePrice")).doubleValue());
            eventService.updateEvent(id, existing);
            res.sendSuccess(existing);
        } catch (EventNotFoundException e) {
            res.sendError(404, e.getMessage());
        } catch (Exception e) {
            res.sendError(400, "Bad Request: " + e.getMessage());
        }
    }
}