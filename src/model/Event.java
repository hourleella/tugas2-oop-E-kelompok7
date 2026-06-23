package model;

import java.util.Map;

public abstract class Event {
    private String id;
    private String type;
    private String name;
    private String venueId;
    private String organizerId;
    private String date;
    private double basePrice;
    private Map<String, Integer> capacities;

    public Event() {
    }

    public Event(String id, String type, String name, String venueId, String organizerId, String date,
            double basePrice, Map<String, Integer> capacities) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.venueId = venueId;
        this.organizerId = organizerId;
        this.date = date;
        this.basePrice = basePrice;
        this.capacities = capacities;
    }

    public abstract double calculateTicketPrice(String category);

    public String getId() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Map<String, Integer> getCapacities() {
        return capacities;
    }

    public void setCapacities(Map<String, Integer> capacities) {
        this.capacities = capacities;
    }
}
