package exception;

public class TicketSoldOutException extends Exception{
    public TicketSoldOutException() {
        super("Ticket not found.");
    }

    public TicketSoldOutException(String message) {
        super(message);
    } 
}