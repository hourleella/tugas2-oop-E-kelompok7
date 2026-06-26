package handler;

import server.Request;
import server.Response;
import server.Server;
import service.TicketService;
import model.Ticket;
import exception.TicketSoldOutException;
import exception.RefundNotAllowedException;

import java.util.Map;

public class TicketHandler {
    private final TicketService ticketService;

    public TicketHandler(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void registerRoutes(Server server) {
        server.get("/api/tickets", this::getAllTickets);
        server.get("/api/tickets/{id}", this::getTicketById);
        server.post("/api/tickets", this::buyTicket);
        server.put("/api/tickets/{id}/refund", this::refundTicket);
        server.get("/api/reports/sales", this::getSalesReport);
    }

    private void getAllTickets(Request req, Response res) {
        try {
            String eventId = req.getQueryParam("eventId");
            String userId = req.getQueryParam("userId");
            String status = req.getQueryParam("status");
            res.sendSuccess(ticketService.getAllTickets(eventId, userId, status));
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void getTicketById(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            res.sendSuccess(ticketService.getTicketById(id));
        } catch (Exception e) {
            res.sendError(404, "Ticket not found.");
        }
    }

    private void buyTicket(Request req, Response res) {
        try {
            Map<String, Object> body = req.getJSON();
            Ticket ticket = new Ticket();
            ticket.setEventId((String) body.get("eventId"));
            ticket.setUserId((String) body.get("userId"));
            ticket.setCategory((String) body.get("category"));
            ticket.setQuantity((int) body.get("quantity"));

            ticketService.buyTicket(ticket);
            res.sendSuccess("Ticket purchased successfully.");
        } catch (TicketSoldOutException e) {
            res.sendError(400, e.getMessage());
        } catch (Exception e) {
            res.sendError(500, "Failed to process ticket purchase: " + e.getMessage());
        }
    }

    private void refundTicket(Request req, Response res) {
        try {
            String id = req.getPathParam("id");
            ticketService.refundTicket(id);

            res.sendSuccess("Ticket refunded successfully.");
        } catch (RefundNotAllowedException e) {
            res.sendError(400, e.getMessage());
        } catch (Exception e) {
            res.sendError(500, "Failed to process refund: " + e.getMessage());
        }
    }

    private void getSalesReport(Request req, Response res) {
        try {
            String eventId = req.getQueryParam("eventId");
            res.sendSuccess(ticketService.getSalesReportByEvent(eventId));
        } catch (Exception e) {
            res.sendError(500, "Internal Server Error: " + e.getMessage());
        }
    }
}