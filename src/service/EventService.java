package service;

import model.Event;
import model.User;
import model.Venue;
import repository.EventRepository;
import repository.UserRepository;
import repository.VenueRepository;

import java.util.List;
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
        boolean isBentrok = eventRepository.checkScheduleConflict(event.getVenueId(), event.getDate());
        if (isBentrok) {
            throw new IllegalArgumentException("Error : venue sudah digunakan oleh event lain pada tanggal yang sama.");
        }

        eventRepository.save(event, capacities);
    }

    public Event getEventById(String id) {
        Event event = eventRepository.findById(id);
        if (event == null) {
            throw new IllegalArgumentException("Error : Event ID " + id + " tidak ditemukan");
        }
        return event;
    }

    public List<Event> getAllEvents(String type, String dateFrom) {
        return eventRepository.findAll(type, dateFrom);
    }
}
