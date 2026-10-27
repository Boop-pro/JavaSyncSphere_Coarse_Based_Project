package exception;

/**
 * Unchecked exception thrown when registering a member with an ID that already exists.
 * Demonstrates Unit III concept: Custom Unchecked Exception (extends RuntimeException).
 */
public class DuplicateMemberException extends RuntimeException {

    public DuplicateMemberException(String message) {
        super(message);
    }

    public DuplicateMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}
