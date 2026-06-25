package exception;

public class RefundNotAllowedException extends Exception {
    public RefundNotAllowedException() {
        super("Refund is not allowed for this ticket.");
    }

    public RefundNotAllowedException(String message) {
        super(message);
    }   
}