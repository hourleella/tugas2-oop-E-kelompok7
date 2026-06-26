package service;

import model.Event;
import model.User;
import model.Venue;
import repository.EventRepository;
import repository.UserRepository;
import repository.VenueRepository;
import exception.EventNotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.SQLException;
import java.util.Map;

public class EventService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    public void createEvent(Event event, Map<String, Integer> capacities) {
        try{
            User organizer = userRepository.findById(event.getOrganizerId());
            if (organizer == null || !"organizer".equalsIgnoreCase(organizer.getRole())) {
                throw new IllegalArgumentException("Error : User bukan organizer atau tidak ditemukan");
            }
            Venue venue = venueRepository.findById(event.getVenueId());
            if (venue == null) {
                throw new IllegalArgumentException("Error : Venue tidak ditemukan");
            }

            int totalKapasitasInput = 0;
            for (int kapasitasKategori : capacities.values()) {
                totalKapasitasInput += kapasitasKategori;
            }
            if (totalKapasitasInput > venue.getMaxCapacity()) {
                throw new IllegalArgumentException("Error : Total kapasitas kategori tiket melebihi kapasitas venue (" + venue.getMaxCapacity() + ").");
            }
            if(eventRepository.venueIsBooked(event.getVenueId(), event.getDate())) {
                throw new IllegalArgumentException("Error : Venue sudah dibooking pada tanggal tersebut.");
            }

            eventRepository.save(event, capacities);
        } catch (SQLException e) {
            throw new RuntimeException("Error database saat membuat event: " + e.getMessage(), e);
        }
    }

    public Event getEventTotalTiketPrice(String eventId) throws EventNotFoundException {
        try {
            Event event = eventRepository.findById(eventId);
            if (event == null) {
                throw new EventNotFoundException("Event ID " + eventId + " tidak ditemukan");
            }
            return event;
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil data event" + e.getMessage());
        }
    }

    public Event getEventById(String id) throws EventNotFoundException {
        return getEventByIdInternal(id);
    }

    public List<Event> getAllEvents(String type, String dateFrom) {
        try {
            return eventRepository.findAll(type, dateFrom);
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil data event" + e.getMessage());
        }
    }

    public List<Map<String, Object>> getPriceSummaryReport() {
        try {
            List<Event> events = eventRepository.findAll(null, null);
            List<Map<String, Object>> result = new ArrayList<>();

            for (Event event : events) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("id", event.getId());
                entry.put("name", event.getName());
                entry.put("type", event.getType());
                
                Map<String, Double> prices = new HashMap<>();
                for (String category : event.getAvailableCategories()) {
                    prices.put(category, event.calculateTicketPrice(category));
                }
                entry.put("prices", prices);
                result.add(entry);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil price summary" + e.getMessage());
        }
    }

    public void updateEvent(String id, Event updatedEvent) throws EventNotFoundException {
        try {
            Event existingEvent = eventRepository.findById(id);
            if (existingEvent == null) {
                throw new EventNotFoundException("Event ID " + id + " tidak ditemukan");
            }
            eventRepository.update(updatedEvent);
        } catch (SQLException e) {
            throw new RuntimeException("Error database saat memperbarui event: " + e.getMessage(), e);
        }
    }
}
