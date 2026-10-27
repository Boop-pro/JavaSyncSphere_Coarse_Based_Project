package app;

import exception.DuplicateMemberException;
import exception.InvalidContributionException;
import exception.MemberNotFoundException;
import model.*;
import repository.Repository;
import service.ContributionService;

/**
 * Automated System and Verification Test Suite for SyncSphere.
 * Executes the exact workflow required in Section 25 of the Master Development Prompt.
 */
public class SyncSphereSystemTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private static void assertTrue(String testName, boolean condition, String details) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.printf("[PASS] %-50s | %s%n", testName, details);
        } else {
            System.err.printf("[FAIL] %-50s | FAILED: %s%n", testName, details);
            throw new AssertionError("Test failed: " + testName + " - " + details);
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("       SYNCSHPERE — COMPREHENSIVE VERIFICATION TEST SUITE        ");
        System.out.println("==================================================================");

        ContributionService service = new ContributionService();
        Member currentActor = null;

        // 1. First-Run State Verification
        assertTrue("Initial Member Repository Empty",
                service.getMemberRepository().isEmpty(),
                "Repository is empty on initial startup (no hardcoded data)");

        // 2. Create First Team Lead
        Member lead = service.registerMember(null, "TL-001", "Alex Morgan", "alex.lead@syncsphere.edu", "Team Lead");
        assertTrue("First Team Lead Created",
                lead instanceof TeamLead && lead.canManageTeam(),
                "Created TL-001: " + lead.getName() + " with TeamLead privileges");

        currentActor = lead;

        // 3. Register Student A & Student B
        Member studentA = service.registerMember(currentActor, "STU-101", "Bob Smith", "bob.smith@syncsphere.edu", "Student Member");
        Member studentB = service.registerMember(currentActor, "STU-102", "Charlie Davis", "charlie.d@syncsphere.edu", "Student Member");

        assertTrue("Student Members Registered",
                studentA instanceof StudentMember && studentB instanceof StudentMember,
                "Registered STU-101 (Bob Smith) and STU-102 (Charlie Davis)");

        assertTrue("Roster Size Verification",
                service.getMemberRepository().size() == 3,
                "Total team members is exactly 3");

        // 4. Test Duplicate Member Registration (Should throw DuplicateMemberException)
        boolean duplicateCaught = false;
        try {
            service.registerMember(currentActor, "STU-101", "Duplicate Bob", "bob2@syncsphere.edu", "Student Member");
        } catch (DuplicateMemberException ex) {
            duplicateCaught = true;
        }
        assertTrue("Duplicate Member Exception Check",
                duplicateCaught,
                "DuplicateMemberException thrown when attempting to re-register STU-101");

        // 5. Test Non-existent Member Lookup
        boolean missingCaught = false;
        try {
            service.findMemberById("UNKNOWN-999");
        } catch (MemberNotFoundException ex) {
            missingCaught = true;
        }
        assertTrue("Member Not Found Exception Check",
                missingCaught,
                "MemberNotFoundException thrown when querying non-existent ID");

        // 6. Team Lead logs initial contribution for self
        try {
            Contribution c1 = service.recordContribution(currentActor, "TL-001", 12.0, "Architecture & project planning");
            assertTrue("Team Lead Contribution Logged",
                    c1 != null && lead.getTotalHours() == 12.0,
                    "Logged 12.0 hours for TL-001; new total: " + lead.getTotalHours());
        } catch (Exception ex) {
            assertTrue("Team Lead Contribution", false, ex.getMessage());
        }

        // 7. Team Lead creates and assigns tasks
        Task task1 = null;
        Task task2 = null;
        try {
            task1 = service.createTask(currentActor, "Develop Database Repository Layer", "Implement generic array storage", "STU-101");
            task2 = service.createTask(currentActor, "Implement UI Dashboard", "Create modern FlatLaf cards", "STU-102");

            assertTrue("Task Creation and Assignment",
                    task1 != null && task1.getAssignedMemberId().equals("STU-101") && task1.getStatus().equals(Task.STATUS_PENDING),
                    "Created " + task1.getTaskId() + " assigned to STU-101 with status PENDING");
        } catch (Exception ex) {
            assertTrue("Task Creation", false, ex.getMessage());
        }

        // 8. Logout Team Lead and Login as Student A (Bob Smith)
        currentActor = null; // Logout
        try {
            currentActor = service.findMemberById("STU-101");
            assertTrue("Login as Student A (STU-101)",
                    currentActor != null && currentActor.getId().equals("STU-101") && !currentActor.canManageTeam(),
                    "Successfully switched context to Student A (Bob Smith)");
        } catch (Exception ex) {
            assertTrue("Login Student A", false, ex.getMessage());
        }

        // 9. Student A tries unauthorized action (register member) -> SecurityException
        boolean unauthorizedRegister = false;
        try {
            service.registerMember(currentActor, "STU-103", "Hacker", "hacker@test.com", "Student Member");
        } catch (SecurityException ex) {
            unauthorizedRegister = true;
        }
        assertTrue("Student Cannot Register Members",
                unauthorizedRegister,
                "SecurityException prevented Student A from registering members");

        // 10. Student A tries to log contribution for Student B -> SecurityException
        boolean unauthorizedContrib = false;
        try {
            service.recordContribution(currentActor, "STU-102", 5.0, "Logging for Charlie");
        } catch (SecurityException ex) {
            unauthorizedContrib = true;
        } catch (Exception ignored) {}
        assertTrue("Student Cannot Log for Others",
                unauthorizedContrib,
                "SecurityException prevented Student A from logging hours for Student B");

        // 11. Student A logs invalid hours (negative / zero) -> InvalidContributionException
        boolean invalidHoursCaught = false;
        try {
            service.recordContribution(currentActor, "STU-101", -3.5, "Negative hours test");
        } catch (InvalidContributionException ex) {
            invalidHoursCaught = true;
        } catch (Exception ignored) {}
        assertTrue("Invalid Contribution Hours Check",
                invalidHoursCaught,
                "InvalidContributionException thrown for negative hours (-3.5)");

        // 12. Student A logs valid own contribution
        try {
            Contribution cBob = service.recordContribution(currentActor, "STU-101", 14.5, "Built generic repository array buffer");
            assertTrue("Student A Logs Own Contribution",
                    cBob != null && currentActor.getTotalHours() == 14.5,
                    "Student A logged 14.5 hours; total is now 14.5");
        } catch (Exception ex) {
            assertTrue("Student A Contrib", false, ex.getMessage());
        }

        // 13. Student A marks their assigned task completed
        try {
            service.completeTask(currentActor, task1.getTaskId());
            assertTrue("Student A Completes Task",
                    task1.getStatus().equals(Task.STATUS_COMPLETED),
                    "Task " + task1.getTaskId() + " status transitioned to COMPLETED");
        } catch (Exception ex) {
            assertTrue("Complete Task", false, ex.getMessage());
        }

        // 14. Student A tries to approve their own completed task -> SecurityException
        boolean unauthorizedApproval = false;
        try {
            service.approveTask(currentActor, task1.getTaskId());
        } catch (SecurityException ex) {
            unauthorizedApproval = true;
        }
        assertTrue("Student Cannot Approve Tasks",
                unauthorizedApproval,
                "SecurityException prevented Student A from approving completed task");

        // 15. Logout Student A and Login as Team Lead
        currentActor = null; // Logout
        try {
            currentActor = service.findMemberById("TL-001");
            assertTrue("Login as Team Lead (TL-001)",
                    currentActor != null && currentActor.canManageTeam(),
                    "Successfully switched context back to Team Lead (Alex Morgan)");
        } catch (Exception ex) {
            assertTrue("Login TL-001", false, ex.getMessage());
        }

        // 16. Team Lead approves completed task
        try {
            service.approveTask(currentActor, task1.getTaskId());
            assertTrue("Team Lead Approves Task",
                    task1.getStatus().equals(Task.STATUS_APPROVED),
                    "Task " + task1.getTaskId() + " status transitioned to APPROVED");
        } catch (Exception ex) {
            assertTrue("Approve Task", false, ex.getMessage());
        }

        // 17. Verify Repeated Approval is rejected
        boolean repeatedApproveCaught = false;
        try {
            service.approveTask(currentActor, task1.getTaskId());
        } catch (IllegalStateException ex) {
            repeatedApproveCaught = true;
        }
        assertTrue("Repeated Task Approval Rejected",
                repeatedApproveCaught,
                "IllegalStateException prevented re-approving an already APPROVED task");

        // 18. Verify Team Report Statistics and Recursive Calculation
        Team team = service.getTeam();
        Team.TeamStats stats = team.getStats();
        double totalHoursRecursive = stats.calculateTotalHours();
        double expectedHours = 12.0 + 14.5; // 26.5

        assertTrue("Recursive Total Hours Calculation",
                Math.abs(totalHoursRecursive - expectedHours) < 0.001,
                "TeamStats.calculateTotalHours() recursive sum = " + totalHoursRecursive + " hrs (Expected: 26.5 hrs)");

        Member topContributor = stats.getTopContributor();
        assertTrue("Top Contributor Identification",
                topContributor != null && topContributor.getId().equals("STU-101"),
                "Top contributor identified as Bob Smith (14.5 hrs)");

        assertTrue("Task Status Breakdown",
                stats.getTotalTasks() == 2 && stats.getApprovedTasksCount() == 1 && stats.getPendingTasksCount() == 1,
                "Total Tasks: 2 | Approved: 1 | Pending: 1");

        // 19. Logout Team Lead and Login as Student B (Charlie Davis)
        currentActor = null; // Logout
        try {
            currentActor = service.findMemberById("STU-102");
            Repository<Contribution> charlieLogs = service.getContributionsForActor(currentActor);
            Repository<Task> charlieTasks = service.getTasksForMember(currentActor.getId());

            assertTrue("Student B User-Specific Isolation",
                    currentActor.getTotalHours() == 0.0 && charlieLogs.isEmpty() && charlieTasks.size() == 1,
                    "Student B sees 0.0 hrs, 0 logs, and only their 1 assigned pending deliverable");
        } catch (Exception ex) {
            assertTrue("Login Student B", false, ex.getMessage());
        }

        // 20. Gradable<T> Polymorphic Grade Calculation
        try {
            Member bob = service.findMemberById("STU-101");
            String grade = bob.calculateGrade();
            assertTrue("Polymorphic Grade Calculation (Gradable<T>)",
                    grade != null && grade.contains("B (Good)"),
                    "Bob Smith (14.5 hrs) calculated grade: " + grade);
        } catch (Exception ex) {
            assertTrue("Grade calculation", false, ex.getMessage());
        }

        System.out.println("==================================================================");
        System.out.println("VERIFICATION SUMMARY: " + testsPassed + " / " + testsRun + " TESTS PASSED (100%)");
        System.out.println("==================================================================");
    }
}
