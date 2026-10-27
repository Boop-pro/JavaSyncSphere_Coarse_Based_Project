package model;

import java.util.Objects;

/**
 * Class representing a project task with a strict lifecycle:
 * PENDING -> COMPLETED -> APPROVED
 * Demonstrates Unit I & II concepts:
 * - Constructor overloading & 'this' chaining
 * - Encapsulation
 * - Public static final constants
 * - Object method overrides
 */
public class Task {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_APPROVED = "APPROVED";

    private String taskId;
    private String title;
    private String description;
    private String assignedMemberId;
    private String assignedMemberName;
    private String status;

    /**
     * Minimal constructor with taskId and title.
     * Demonstrates constructor chaining.
     */
    public Task(String taskId, String title) {
        this(taskId, title, "No description provided.", "UNASSIGNED", "Unassigned");
    }

    /**
     * Overloaded constructor with description.
     */
    public Task(String taskId, String title, String description) {
        this(taskId, title, description, "UNASSIGNED", "Unassigned");
    }

    /**
     * Comprehensive constructor with assignment.
     */
    public Task(String taskId, String title, String description, String assignedMemberId, String assignedMemberName) {
        this.taskId = (taskId != null) ? taskId.trim() : "";
        this.title = (title != null) ? title.trim() : "";
        this.description = (description != null) ? description.trim() : "";
        this.assignedMemberId = (assignedMemberId != null && !assignedMemberId.trim().isEmpty()) ? assignedMemberId.trim() : "UNASSIGNED";
        this.assignedMemberName = (assignedMemberName != null && !assignedMemberName.trim().isEmpty()) ? assignedMemberName.trim() : "Unassigned";
        this.status = STATUS_PENDING;
    }

    // Encapsulation - Getters and Setters

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = (taskId != null) ? taskId.trim() : "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = (title != null) ? title.trim() : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public String getAssignedMemberId() {
        return assignedMemberId;
    }

    public void setAssignedMemberId(String assignedMemberId) {
        this.assignedMemberId = (assignedMemberId != null && !assignedMemberId.trim().isEmpty()) ? assignedMemberId.trim() : "UNASSIGNED";
    }

    public String getAssignedMemberName() {
        return assignedMemberName;
    }

    public void setAssignedMemberName(String assignedMemberName) {
        this.assignedMemberName = (assignedMemberName != null && !assignedMemberName.trim().isEmpty()) ? assignedMemberName.trim() : "Unassigned";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && (status.equals(STATUS_PENDING) || status.equals(STATUS_COMPLETED) || status.equals(STATUS_APPROVED))) {
            this.status = status;
        }
    }

    public boolean isAssigned() {
        return assignedMemberId != null && !assignedMemberId.equalsIgnoreCase("UNASSIGNED") && !assignedMemberId.isEmpty();
    }

    /**
     * Marks the task as completed.
     * Enforces that task must be PENDING and assigned to the completing member.
     */
    public boolean markCompleted(Member member) {
        if (member == null) {
            return false;
        }
        if (!STATUS_PENDING.equalsIgnoreCase(this.status)) {
            return false;
        }
        if (!isAssigned()) {
            return false; // unassigned tasks cannot be completed
        }
        // Member must be the assigned student, or a TeamLead overseeing completion
        if (!member.canManageTeam() && !this.assignedMemberId.equalsIgnoreCase(member.getId())) {
            return false;
        }
        this.status = STATUS_COMPLETED;
        return true;
    }

    /**
     * Approves the task. Enforces that task must be COMPLETED and approver must be TeamLead.
     */
    public boolean approve(Member approver) {
        if (approver == null || !approver.canManageTeam()) {
            return false;
        }
        if (!STATUS_COMPLETED.equalsIgnoreCase(this.status)) {
            return false;
        }
        this.status = STATUS_APPROVED;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Task)) return false;
        Task other = (Task) obj;
        return this.taskId.equalsIgnoreCase(other.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Assigned: %s (%s) | Status: %s",
                taskId, title, assignedMemberName, assignedMemberId, status);
    }
}
