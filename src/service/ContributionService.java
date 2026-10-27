package service;

import exception.DuplicateMemberException;
import exception.InvalidContributionException;
import exception.MemberNotFoundException;
import model.*;
import repository.Repository;

/**
 * Service layer coordinating business logic, validation, and authorization.
 * Demonstrates Unit I, II, and III concepts:
 * - Separation of concerns (Service Layer pattern)
 * - Custom checked & unchecked exception handling (throw, throws)
 * - Role-Based Access Control (RBAC) via runtime polymorphism & instanceof
 * - String validation and defensive programming
 */
public class ContributionService {

    private Repository<Member> memberRepository;
    private Repository<Task> taskRepository;
    private Repository<Contribution> contributionRepository;
    private Team team;

    public ContributionService() {
        this.memberRepository = new Repository<Member>();
        this.taskRepository = new Repository<Task>();
        this.contributionRepository = new Repository<Contribution>();
        this.team = new Team("SyncSphere Project Alpha", memberRepository, taskRepository, contributionRepository);
    }

    public Team getTeam() {
        return team;
    }

    public Repository<Member> getMemberRepository() {
        return memberRepository;
    }

    public Repository<Task> getTaskRepository() {
        return taskRepository;
    }

    public Repository<Contribution> getContributionRepository() {
        return contributionRepository;
    }

    // ==========================================
    // MEMBER MANAGEMENT & AUTHORIZATION
    // ==========================================

    /**
     * Registers a new member.
     * Enforces:
     * - First user may register as TeamLead or Student.
     * - Subsequent registrations require TeamLead privileges.
     * - Input validation (non-empty fields, valid email).
     * - Duplicate ID detection throwing unchecked DuplicateMemberException.
     */
    public Member registerMember(Member actor, String id, String name, String email, String role)
            throws DuplicateMemberException, IllegalArgumentException {

        // Validate basic inputs
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID / Roll Number cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member Name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Please provide a valid email address (must include '@' and '.').");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role must be specified.");
        }

        String cleanId = id.trim();
        String cleanName = name.trim();
        String cleanEmail = email.trim();
        String cleanRole = role.trim();

        // RBAC check: Only TeamLead can register members once the system has users
        if (!memberRepository.isEmpty()) {
            if (actor == null || !actor.canManageTeam()) {
                throw new SecurityException("Access Denied!\nOnly Team Lead can perform this operation.");
            }
        }

        // Duplicate ID check
        for (int i = 0; i < memberRepository.size(); i++) {
            if (memberRepository.get(i).getId().equalsIgnoreCase(cleanId)) {
                throw new DuplicateMemberException("Member with ID '" + cleanId + "' already exists in the system!");
            }
        }

        // Instantiate appropriate concrete subclass (Polymorphism)
        Member newMember;
        if (cleanRole.equalsIgnoreCase("Team Lead")) {
            newMember = new TeamLead(cleanId, cleanName, cleanEmail);
        } else {
            newMember = new StudentMember(cleanId, cleanName, cleanEmail);
        }

        memberRepository.add(newMember);
        return newMember;
    }

    /**
     * Finds a member by their unique ID.
     * Throws checked MemberNotFoundException if not found.
     */
    public Member findMemberById(String id) throws MemberNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            throw new MemberNotFoundException("Member ID cannot be null or empty.");
        }
        String cleanId = id.trim();
        for (int i = 0; i < memberRepository.size(); i++) {
            Member m = memberRepository.get(i);
            if (m.getId().equalsIgnoreCase(cleanId)) {
                return m;
            }
        }
        throw new MemberNotFoundException("Member with ID '" + cleanId + "' not found.");
    }

    // ==========================================
    // CONTRIBUTION MANAGEMENT
    // ==========================================

    /**
     * Records a contribution log.
     * Enforces:
     * - Positive contribution hours (> 0).
     * - Non-empty description.
     * - Target member existence.
     * - Students can only record contributions for themselves.
     */
    public Contribution recordContribution(Member actor, String targetMemberId, double hours, String description)
            throws InvalidContributionException, MemberNotFoundException, SecurityException {

        if (actor == null) {
            throw new SecurityException("Access Denied! Unauthenticated operation.");
        }

        if (targetMemberId == null || targetMemberId.trim().isEmpty()) {
            throw new MemberNotFoundException("Target Member ID must be specified.");
        }

        String cleanTargetId = targetMemberId.trim();

        // RBAC: Students can never submit contributions for another member
        if (!actor.canManageTeam() && !actor.getId().equalsIgnoreCase(cleanTargetId)) {
            throw new SecurityException("Access Denied!\nStudents can only log their own contributions.");
        }

        // Validation for hours
        if (hours <= 0.0) {
            throw new InvalidContributionException("Contribution hours must be greater than zero. Received: " + hours);
        }

        // Validation for description
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidContributionException("Contribution description cannot be empty.");
        }

        // Find member and update cumulative hours
        Member targetMember = findMemberById(cleanTargetId);
        targetMember.addHours(hours);

        Contribution contribution = new Contribution(cleanTargetId, targetMember.getName(), hours, description.trim());
        contributionRepository.add(contribution);

        return contribution;
    }

    /**
     * Returns contributions filtered for the viewing actor.
     * Team Lead sees all records.
     * Student sees only their own records.
     */
    public Repository<Contribution> getContributionsForActor(Member actor) {
        if (actor == null) {
            return new Repository<Contribution>();
        }
        if (actor.canManageTeam()) {
            return contributionRepository;
        }

        // Filter for student
        Repository<Contribution> filtered = new Repository<Contribution>();
        for (int i = 0; i < contributionRepository.size(); i++) {
            Contribution c = contributionRepository.get(i);
            if (c.getMemberId().equalsIgnoreCase(actor.getId())) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    // ==========================================
    // TASK MANAGEMENT & LIFECYCLE
    // ==========================================

    /**
     * Creates a new task.
     * Enforces TeamLead privilege and input validation.
     */
    public Task createTask(Member actor, String title, String description, String assignedMemberId)
            throws MemberNotFoundException, SecurityException, IllegalArgumentException {

        if (actor == null || !actor.canManageTeam()) {
            throw new SecurityException("Access Denied!\nOnly Team Lead can perform this operation.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task Title cannot be empty.");
        }

        String cleanTitle = title.trim();
        String cleanDesc = (description != null) ? description.trim() : "";
        String taskId = "TSK-" + (100 + taskRepository.size() + 1);

        String assignedName = "Unassigned";
        String cleanAssignedId = "UNASSIGNED";

        if (assignedMemberId != null && !assignedMemberId.trim().isEmpty() && !assignedMemberId.equalsIgnoreCase("UNASSIGNED")) {
            Member assignee = findMemberById(assignedMemberId.trim());
            cleanAssignedId = assignee.getId();
            assignedName = assignee.getName();
        }

        Task newTask = new Task(taskId, cleanTitle, cleanDesc, cleanAssignedId, assignedName);
        taskRepository.add(newTask);
        return newTask;
    }

    /**
     * Assigns or reassigns an existing task.
     * Enforces TeamLead privilege.
     */
    public void assignTask(Member actor, String taskId, String targetMemberId)
            throws MemberNotFoundException, SecurityException, IllegalArgumentException {

        if (actor == null || !actor.canManageTeam()) {
            throw new SecurityException("Access Denied!\nOnly Team Lead can perform this operation.");
        }

        Task task = findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task with ID '" + taskId + "' not found.");
        }

        if (targetMemberId == null || targetMemberId.equalsIgnoreCase("UNASSIGNED") || targetMemberId.trim().isEmpty()) {
            task.setAssignedMemberId("UNASSIGNED");
            task.setAssignedMemberName("Unassigned");
        } else {
            Member member = findMemberById(targetMemberId.trim());
            task.setAssignedMemberId(member.getId());
            task.setAssignedMemberName(member.getName());
        }
    }

    /**
     * Marks a pending task as completed.
     * Enforces:
     * - Task must exist and be PENDING.
     * - Task must be assigned.
     * - Student can only mark their own assigned tasks completed.
     */
    public void completeTask(Member actor, String taskId) throws SecurityException, IllegalStateException {
        if (actor == null) {
            throw new SecurityException("Access Denied! Unauthenticated operation.");
        }

        Task task = findTaskById(taskId);
        if (task == null) {
            throw new IllegalStateException("Task with ID '" + taskId + "' not found.");
        }

        if (!Task.STATUS_PENDING.equalsIgnoreCase(task.getStatus())) {
            throw new IllegalStateException("Only tasks in PENDING status can be marked as COMPLETED.");
        }

        if (!task.isAssigned()) {
            throw new IllegalStateException("An unassigned task cannot be completed. It must be assigned to a member first.");
        }

        // Student can only complete their own task
        if (!actor.canManageTeam() && !task.getAssignedMemberId().equalsIgnoreCase(actor.getId())) {
            throw new SecurityException("Access Denied!\nYou can only mark your own assigned tasks as completed.");
        }

        boolean success = task.markCompleted(actor);
        if (!success) {
            throw new IllegalStateException("Failed to complete task due to invalid status or assignment.");
        }

        // Update student's completed tasks metric
        try {
            Member assignee = findMemberById(task.getAssignedMemberId());
            if (assignee instanceof StudentMember) {
                ((StudentMember) assignee).incrementCompletedTasks();
            }
        } catch (MemberNotFoundException ignored) {
        }
    }

    /**
     * Approves a completed task.
     * Enforces:
     * - Only TeamLead can approve.
     * - Task must be COMPLETED.
     * - Cannot approve already APPROVED task.
     */
    public void approveTask(Member actor, String taskId) throws SecurityException, IllegalStateException {
        if (actor == null || !actor.canManageTeam()) {
            throw new SecurityException("Access Denied!\nOnly Team Lead can perform this operation.");
        }

        Task task = findTaskById(taskId);
        if (task == null) {
            throw new IllegalStateException("Task with ID '" + taskId + "' not found.");
        }

        if (Task.STATUS_APPROVED.equalsIgnoreCase(task.getStatus())) {
            throw new IllegalStateException("Task is already approved! Repeated approvals are not permitted.");
        }

        if (!Task.STATUS_COMPLETED.equalsIgnoreCase(task.getStatus())) {
            throw new IllegalStateException("Cannot approve task: Task must be in COMPLETED status before approval.");
        }

        boolean success = task.approve(actor);
        if (!success) {
            throw new IllegalStateException("Approval could not be processed.");
        }

        if (actor instanceof TeamLead) {
            ((TeamLead) actor).incrementApprovalCount();
        }
    }

    /**
     * Finds a task by ID.
     */
    public Task findTaskById(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }
        for (int i = 0; i < taskRepository.size(); i++) {
            Task t = taskRepository.get(i);
            if (t.getTaskId().equalsIgnoreCase(taskId.trim())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Retrieves tasks assigned to a specific member.
     */
    public Repository<Task> getTasksForMember(String memberId) {
        Repository<Task> list = new Repository<Task>();
        if (memberId == null) return list;
        for (int i = 0; i < taskRepository.size(); i++) {
            Task t = taskRepository.get(i);
            if (memberId.equalsIgnoreCase(t.getAssignedMemberId())) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Retrieves all tasks currently awaiting approval (status: COMPLETED).
     */
    public Repository<Task> getTasksAwaitingApproval() {
        Repository<Task> awaiting = new Repository<Task>();
        for (int i = 0; i < taskRepository.size(); i++) {
            Task t = taskRepository.get(i);
            if (Task.STATUS_COMPLETED.equalsIgnoreCase(t.getStatus())) {
                awaiting.add(t);
            }
        }
        return awaiting;
    }
}
