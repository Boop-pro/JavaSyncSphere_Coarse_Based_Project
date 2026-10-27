package model;

import contracts.Gradable;
import java.util.Objects;

/**
 * Abstract base class representing a team member.
 * Demonstrates Unit I & II concepts:
 * - Abstract class
 * - Encapsulation (private fields, public getters/setters)
 * - Constructor overloading & 'this' chaining
 * - Object method overrides (toString, equals, hashCode)
 * - Wrapper classes and String manipulation
 * - Generic interface implementation (Gradable<String>)
 */
public abstract class Member implements Gradable<String> {

    private String id;
    private String name;
    private String email;
    private String role;
    private double totalHours;

    /**
     * Primary constructor with initial zero hours.
     * Demonstrates constructor overloading and 'this' keyword.
     */
    public Member(String id, String name, String email, String role) {
        this(id, name, email, role, 0.0);
    }

    /**
     * Overloaded constructor with initial contribution hours.
     * Demonstrates 'this' invocation and constructor overloading.
     */
    public Member(String id, String name, String email, String role, double totalHours) {
        this.id = (id != null) ? id.trim() : "";
        this.name = (name != null) ? name.trim() : "";
        this.email = (email != null) ? email.trim() : "";
        this.role = (role != null) ? role.trim() : "";
        this.totalHours = Math.max(0.0, totalHours);
    }

    // Encapsulation - Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = (role != null) ? role.trim() : "";
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = Math.max(0.0, totalHours);
    }

    /**
     * Adds hours to the member's total contribution hours.
     * Uses wrapper Double for value demonstration.
     */
    public void addHours(double hours) {
        if (hours > 0.0) {
            Double current = Double.valueOf(this.totalHours);
            Double added = Double.valueOf(hours);
            this.totalHours = current.doubleValue() + added.doubleValue();
        }
    }

    /**
     * Abstract method to be overridden by subclasses.
     * Demonstrates runtime polymorphism.
     */
    public abstract String getRoleSpecificDetails();

    /**
     * Checks if this member has team management authorization.
     */
    public abstract boolean canManageTeam();

    /**
     * Override of Object.equals demonstrating casting and instanceof.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Member)) return false;
        Member other = (Member) obj;
        return this.id.equalsIgnoreCase(other.id);
    }

    /**
     * Override of Object.hashCode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id.toLowerCase());
    }

    /**
     * Override of Object.toString.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %.1f hrs", id, name, role, totalHours);
    }

    /**
     * Implementation of Reportable interface method.
     */
    @Override
    public void generateReport() {
        System.out.println("Member Report for: " + name + " (" + role + ") | Total Hours: " + totalHours);
    }
}
