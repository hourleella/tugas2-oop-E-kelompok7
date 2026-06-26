package service;

import model.Venue;
import repository.VenueRepository;

import java.sql.SQLException;
import java.util.List;

public class VenueService {
    private VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public void createVenue(Venue venue) {
        try {
            if (venue == null){
                throw new IllegalArgumentException("Error : Data venue tidak boleh null.");
            } 
            if (venue.getName() == null || venue.getName().trim().isEmpty() || venue.getAddress() == null || venue.getAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Error :Nama dan alamat tempat harus diisi.");
            }
            if (venue.getMaxCapacity() <= 0) {
                throw new IllegalArgumentException("Error : Kapasitas maksimum harus lebih besar dari 0.");
            }

            venueRepository.save(venue);
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat membuat venue - " + e.getMessage(), e);
        }
    }

    public List<Venue> getAllVenues() {
        try {
            return venueRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat mengambil semua venue - " + e.getMessage(), e);
        }
    }

    public List<String[]> getEventByVenueId(String venueId) {
        try {
            if (venueId == null || venueId.trim().isEmpty()) {
                throw new IllegalArgumentException("Error : ID venue tidak boleh kosong.");
            }
            Venue venue = venueRepository.findById(venueId);
            if (venue == null) {
                throw new IllegalArgumentException("Error : Venue dengan ID " + venueId + " tidak ditemukan.");
            }
            return venueRepository.getEventsByVenueId(venueId);
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat mengambil event di venue - " + e.getMessage(), e);
        }
    }

    public void updateVenue(String id, Venue updatedVenue) {
        try {
            Venue existingVenue = venueRepository.findById(id);
            if (existingVenue == null) {
                throw new IllegalArgumentException("Error : Venue dengan ID tersebut tidak ditemukan.");
            }
            venueRepository.update(updatedVenue);
        } catch (SQLException e) {
            throw new RuntimeException("Error : terjadi kesalahan database saat update venue -" + e.getMessage(), e);
        }
    }
}
