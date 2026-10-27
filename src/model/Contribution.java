package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Entity representing an individual member's contribution log.
 * Demonstrates Unit I concepts:
 * - Encapsulation
 * - Constructor overloading
 * - Wrapper classes
 * - String formatting
 */
public class Contribution {

    private String contributionId;
    private String memberId;
    private String memberName;
    private double hours;
    private String description;
    private String dateTimestamp;

    public Contribution(String memberId, String memberName, double hours, String description) {
        this("LOG-" + System.currentTimeMillis() % 100000, memberId, memberName, hours, description);
    }

    public Contribution(String contributionId, String memberId, String memberName, double hours, String description) {
        this.contributionId = (contributionId != null) ? contributionId.trim() : "LOG-" + System.currentTimeMillis();
        this.memberId = (memberId != null) ? memberId.trim() : "";
        this.memberName = (memberName != null) ? memberName.trim() : "";
        this.hours = Math.max(0.0, hours);
        this.description = (description != null) ? description.trim() : "";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.dateTimestamp = sdf.format(new Date());
    }

    public String getContributionId() {
        return contributionId;
    }

    public void setContributionId(String contributionId) {
        this.contributionId = contributionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = (memberId != null) ? memberId.trim() : "";
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = (memberName != null) ? memberName.trim() : "";
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = Math.max(0.0, hours);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public String getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(String dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Contribution)) return false;
        Contribution other = (Contribution) obj;
        return this.contributionId.equalsIgnoreCase(other.contributionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contributionId.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (ID: %s) logged %.1f hrs: %s (%s)",
                contributionId, memberName, memberId, hours, description, dateTimestamp);
    }
}
