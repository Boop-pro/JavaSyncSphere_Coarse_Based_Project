package contracts;

/**
 * Generic interface extending Reportable.
 * Demonstrates Unit II concepts: Interface inheritance and Generic Interfaces.
 *
 * @param <T> Type of grade returned (e.g. String or Character)
 */
public interface Gradable<T> extends Reportable {
    T calculateGrade();
}
