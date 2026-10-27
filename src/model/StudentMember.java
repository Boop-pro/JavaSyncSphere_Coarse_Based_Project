package model;

/**
 * Concrete class representing a student team member.
 * Demonstrates Unit II concepts:
 * - Inheritance (extends Member)
 * - 'super' keyword in constructors and methods
 * - Method overriding
 * - Implementation of generic method from Gradable<String>
 */
public class StudentMember extends Member {

    private String academicTrack;
    private int completedTasksCount;

    public StudentMember(String id, String name, String email) {
        super(id, name, email, "Student Member");
        this.academicTrack = "Computer Science Core";
        this.completedTasksCount = 0;
    }

    public StudentMember(String id, String name, String email, double initialHours) {
        super(id, name, email, "Student Member", initialHours);
        this.academicTrack = "Computer Science Core";
        this.completedTasksCount = 0;
    }

    public String getAcademicTrack() {
        return academicTrack;
    }

    public void setAcademicTrack(String academicTrack) {
        this.academicTrack = academicTrack;
    }

    public int getCompletedTasksCount() {
        return completedTasksCount;
    }

    public void incrementCompletedTasks() {
        this.completedTasksCount++;
    }

    public void setCompletedTasksCount(int count) {
        this.completedTasksCount = Math.max(0, count);
    }

    @Override
    public boolean canManageTeam() {
        return false;
    }

    @Override
    public String getRoleSpecificDetails() {
        return "Student Contributor | Track: " + academicTrack + " | Completed Tasks: " + completedTasksCount;
    }

    /**
     * Calculates academic contribution grade based on logged hours and completed tasks.
     * Demonstrates switch/if-else logic and generic return from Gradable<String>.
     */
    @Override
    public String calculateGrade() {
        double hours = getTotalHours();
        if (hours >= 25.0) {
            return "A+ (Outstanding)";
        } else if (hours >= 18.0) {
            return "A (Excellent)";
        } else if (hours >= 12.0) {
            return "B (Good)";
        } else if (hours >= 6.0) {
            return "C (Satisfactory)";
        } else {
            return "D (Needs Improvement)";
        }
    }

    @Override
    public void generateReport() {
        super.generateReport();
        System.out.println("  -> Track: " + academicTrack + ", Tasks Completed: " + completedTasksCount + ", Grade: " + calculateGrade());
    }

    @Override
    public String toString() {
        return super.toString() + " [Student - " + academicTrack + "]";
    }
}
