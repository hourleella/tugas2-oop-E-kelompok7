package service;

import model.Event;
import model.User;
import model.Venue;
import repository.EventRepository;
import repository.UserRepository;
import repository.VenueRepository;

import java.util.List;
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

    public double getEventTotalTiketPrice(String eventId) {
        try {
            Event event = eventRepository.findById(eventId);
            if (event == null) {
                throw new IllegalArgumentException("Error : Event ID " + eventId + " tidak ditemukan");
            }

            double totalEventRevenue = 0;

            for (String category : event.getCapacities().keySet()){
                double pricePerCategory = event.calculateTicketPrice(category);
                int capacity = event.getCapacities().get(category);
                totalEventRevenue += pricePerCategory * capacity;
            }
            return totalEventRevenue;
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat menghitung total harga tiket" + e.getMessage());
        }
    }

    public Event getEventById(String id) {
        try{
            Event event = eventRepository.findById(id);
            if (event == null) {
                throw new IllegalArgumentException("Error : Event ID " + id + " tidak ditemukan");
            }
            return event;
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil data event" + e.getMessage());
        }
    }

    public List<Event> getAllEvents(String type, String dateFrom) {
        try {
            return eventRepository.findAll(type, dateFrom);
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil data event" + e.getMessage());
        }
    }
}
