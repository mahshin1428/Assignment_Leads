package Mahshin.Leads.SpringBootAssignment.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}