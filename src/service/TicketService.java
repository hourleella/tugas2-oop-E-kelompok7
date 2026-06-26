package service;

import model.Event;
import model.Ticket;
import model.User;
import model.Refundable;
import repository.EventRepository;
import repository.UserRepository;
import repository.TicketRepository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TicketService {
    private TicketRepository ticketRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;


    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public void buyTicket(Ticket ticket) {
        try{
            User customer = userRepository.findById(ticket.getUserId());
            if (customer == null){
             throw new IllegalArgumentException("Error : User tidak ditemukan");
            }
            Event event = eventRepository.findById(ticket.getEventId());
            if (event == null) {
                throw new IllegalArgumentException("Error : Event tidak ditemukan");
            }
            int sisaTiket = ticketRepository.getRemainingCapacity(ticket.getEventId(), ticket.getCategory());
            if (sisaTiket < ticket.getQuantity()) {
                throw new IllegalArgumentException("Error : Tiket  kategori '" + ticket.getCategory() + "' tidak cukup. Sisa tiket: " + sisaTiket);
            }
            double hargaSatuan = event.calculateTicketPrice(ticket.getCategory());

            ticket.setUnitPrice(hargaSatuan);
            ticket.setTotalPrice(hargaSatuan * ticket.getQuantity());
            ticket.setStatus("active");

            ticketRepository.save(ticket);
            ticketRepository.incrementFilledCapacity(ticket.getEventId(), ticket.getCategory(), ticket.getQuantity());

        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat membeli tiket" + e.getMessage());
        }
    }

    public void refundTicket(String ticketId) {
        try{
            Ticket ticket = ticketRepository.findById(ticketId);
            if (ticket == null) {
                throw new IllegalArgumentException("Error : Ticket ID " + ticketId + " tidak ditemukan");
            }
            if ("refunded".equalsIgnoreCase(ticket.getStatus())) {
                throw new IllegalArgumentException("Error : Ticket ID " + ticketId + " tidak dapat direfund karena statusnya sudah pernah direfund");
            }

            Event event = eventRepository.findById(ticket.getEventId());
            
            if (!(event instanceof Refundable)) {
                throw new IllegalArgumentException("Error : Event tipe ini tidak mendukung fitur refund");
            }

            Refundable refundableEvent = (Refundable) event;
            LocalDate hariIni = LocalDate.now();
            LocalDate tanggalEvent = LocalDate.parse(event.getDate());
            long selisihHari = ChronoUnit.DAYS.between(hariIni, tanggalEvent);

            if (selisihHari < 0) {
                throw new IllegalArgumentException("Error : Refund tidak dapat dilakukan karena sudah melewati batas waktu refund");
            }

            double persentaseRefund = refundableEvent.calculateRefund((int) selisihHari);
            if (persentaseRefund <= 0) {
                throw new IllegalArgumentException("Error : Kebijakan waktu refund hangus (Kompensasi 0%)");
            }

            double jumlahRefundUang = (persentaseRefund / 100) * ticket.getTotalPrice();
            ticketRepository.updateRefund(ticketId, jumlahRefundUang);

        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat melakukan refund tiket" + e.getMessage());
        }
    }

    public Ticket getTicketById(String id) {
        try {
            Ticket ticket = ticketRepository.findById(id);
            if (ticket == null) {
                throw new IllegalArgumentException("Error : Ticket ID " + id + " tidak ditemukan");
            }
            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil tiket" + e.getMessage());
        }
    }

    public List<Ticket> getTicketsByUserId(String userId) {
        try {
            return ticketRepository.findAll(null, userId, null);
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil data user" + e.getMessage());
        }
    }

    public Map<String, Object> getSalesReportByEvent(String eventId) {
        try {
            Event event = eventRepository.findById(eventId);
            if (event == null) {
                throw new IllegalArgumentException("Error : Event ID " + eventId + " tidak ditemukan");
            }

            List<Ticket> tickets = ticketRepository.findAll(eventId, null, null);

            int totalTicketsSold = 0;
            double totalRevenue = 0;
            Map<String, Integer> soldByCategory = new HashMap<>();
            Map<String, Double> revenueByCategory = new HashMap<>();

            for (Ticket ticket : tickets) {
                if ("active".equals(ticket.getStatus())) {
                    totalTicketsSold += ticket.getQuantity();
                    totalRevenue += ticket.getTotalPrice();
                    soldByCategory.merge(ticket.getCategory(), ticket.getQuantity(), Integer::sum);
                    revenueByCategory.merge(ticket.getCategory(), ticket.getTotalPrice(), Double::sum);
                }
            }

            Map<String, Object> byCategory = new HashMap<>();
            for (String category : soldByCategory.keySet()) {
                Map<String, Object> catData = new HashMap<>();
                catData.put("sold", soldByCategory.get(category));
                catData.put("revenue", revenueByCategory.get(category));
                byCategory.put(category, catData);
            }

            Map<String, Object> report = new HashMap<>();
            report.put("eventId", eventId);
            report.put("eventName", event.getName());
            report.put("totalTicketsSold", totalTicketsSold);
            report.put("totalRevenue", totalRevenue);
            report.put("byCategory", byCategory);

            return report;
        } catch (SQLException e) {
            throw new RuntimeException("Error : Terjadi kesalahan saat mengambil sales report" + e.getMessage());
        }
    }
}
