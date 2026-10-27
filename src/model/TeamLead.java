package model;

/**
 * Concrete class representing a Team Lead member.
 * Demonstrates Unit II concepts:
 * - Inheritance (extends Member)
 * - 'super' keyword in constructors and methods
 * - Method overriding
 * - Role-specific privilege and polymorphic behavior
 */
public class TeamLead extends Member {

    private String leadDepartment;
    private int approvalsReviewedCount;

    public TeamLead(String id, String name, String email) {
        super(id, name, email, "Team Lead");
        this.leadDepartment = "Project Management & Architecture";
        this.approvalsReviewedCount = 0;
    }

    public TeamLead(String id, String name, String email, double initialHours) {
        super(id, name, email, "Team Lead", initialHours);
        this.leadDepartment = "Project Management & Architecture";
        this.approvalsReviewedCount = 0;
    }

    public String getLeadDepartment() {
        return leadDepartment;
    }

    public void setLeadDepartment(String leadDepartment) {
        this.leadDepartment = leadDepartment;
    }

    public int getApprovalsReviewedCount() {
        return approvalsReviewedCount;
    }

    public void incrementApprovalCount() {
        this.approvalsReviewedCount++;
    }

    @Override
    public boolean canManageTeam() {
        return true;
    }

    @Override
    public String getRoleSpecificDetails() {
        return "Team Lead | Dept: " + leadDepartment + " | Approved Tasks Reviewed: " + approvalsReviewedCount;
    }

    /**
     * Calculates leadership evaluation rating.
     */
    @Override
    public String calculateGrade() {
        if (approvalsReviewedCount >= 5 && getTotalHours() >= 15.0) {
            return "Lead A+ (Exemplary Leadership)";
        } else if (getTotalHours() >= 10.0) {
            return "Lead A (High Responsibility)";
        } else {
            return "Lead B (Active Leadership)";
        }
    }

    @Override
    public void generateReport() {
        super.generateReport();
        System.out.println("  -> Leadership: " + leadDepartment + ", Approvals Done: " + approvalsReviewedCount);
    }

    @Override
    public String toString() {
        return super.toString() + " [Lead - " + leadDepartment + "]";
    }
}
