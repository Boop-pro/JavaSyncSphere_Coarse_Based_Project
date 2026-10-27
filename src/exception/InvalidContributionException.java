package exception;

/**
 * Checked exception thrown when contribution hours or descriptions are invalid.
 * Demonstrates Unit III concept: Custom Checked Exception (extends Exception).
 */
public class InvalidContributionException extends Exception {

    public InvalidContributionException(String message) {
        super(message);
    }

    public InvalidContributionException(String message, Throwable cause) {
        super(message, cause);
    }
}
