package contracts;

/**
 * Interface defining contract for objects capable of generating reports.
 * Demonstrates Unit II concepts: Interfaces and public static final constants.
 */
public interface Reportable {
    String REPORT_TYPE = "TEAM REPORT";

    void generateReport();
}
