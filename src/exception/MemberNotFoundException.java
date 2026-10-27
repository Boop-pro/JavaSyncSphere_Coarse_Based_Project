package exception;

/**
 * Checked exception thrown when a member cannot be located by ID.
 * Demonstrates Unit III concept: Custom Checked Exception (extends Exception).
 */
public class MemberNotFoundException extends Exception {

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
