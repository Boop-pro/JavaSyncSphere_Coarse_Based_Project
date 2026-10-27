================================================================================
SYNCSHPERE — TEAM CONTRIBUTION & TASK MANAGEMENT SYSTEM
2nd-Year B.Tech Computer Science Core Java CBP (Course Based Project)
================================================================================

1. PROJECT OVERVIEW
--------------------------------------------------------------------------------
SyncSphere is a complete, production-grade desktop application designed to
streamline project governance, accountability, task distribution, and individual
contribution auditing for academic engineering and software project teams. Built
strictly using Core Java 17+, Java Swing, and FlatLaf Light Look & Feel, SyncSphere
serves as a comprehensive demonstration of 2nd-year B.Tech Core Java syllabus
concepts while offering an intuitive, modern, SaaS-grade desktop experience.


2. PROBLEM STATEMENT
--------------------------------------------------------------------------------
In academic engineering courses, student teams frequently struggle with:
- "Free-rider" problems where contribution distribution is uneven and unverified.
- Lack of transparent logging for individual development hours.
- Ambiguous task assignment and lifecycle status tracking.
- Absence of role-based authorization to prevent unauthorized status changes.
- Difficulties in presenting measurable audit metrics during faculty evaluations.

SyncSphere solves these challenges by providing an in-memory, role-enforced
governance framework where work hours, tasks, and deliverables are tracked from
initial assignment through completion and Team Lead verification.


3. OBJECTIVES
--------------------------------------------------------------------------------
1. Implement a clean 4-tier layered architecture: Model, Generic Repository,
   Service (Authorization & Business Rules), and Swing UI.
2. Demonstrate complete coverage of Units I, II, III, and V of the Core Java
   academic curriculum without relying on external databases or web frameworks.
3. Provide rigorous Role-Based Access Control (RBAC) distinguishing Team Leads
   from Student Members at both the UI and Service layer.
4. Deliver dynamic, live-updating statistics cards, recent activity boards, and
   formal academic audit reports.


4. FEATURES
--------------------------------------------------------------------------------
• First-Run Dynamic Account Creation: System starts cleanly with zero hardcoded
  demo data. If no users exist, prompts for initial account creation.
• Member-ID-Based Authentication: Quick, frictionless classroom login using
  unique student roll numbers / member IDs.
• Role-Based Access Control (RBAC):
  - Team Lead: Full administrative powers (register members, assign tasks, log
    contributions for any member, review & approve deliverables, audit reports).
  - Student Member: Securely restricted to personal dashboard, personal task
    marking, own contribution logging, and personal profile metrics.
• Deliverable Task Lifecycle:
  PENDING (allocated) -> COMPLETED (by student) -> APPROVED (by Team Lead).
• Dynamic Statistical Dashboards:
  - Team Lead: Total Members, Total Tasks, Cumulative Hours, Pending Tasks,
    Queue Awaiting Approval, Approved Deliverables.
  - Student: My Hours, My Assigned Tasks, My Pending Tasks, My Completed Tasks,
    My Approved Tasks.
• Recursive Team Metric Computation: TeamStats utilizes meaningful recursion
  to aggregate total team contribution hours across internal arrays.
• Automated Academic Performance Grading: Polymorphic implementation of generic
  interface Gradable<String> evaluating members into academic tiers (A+, A, B, C, D).
• Official Academic Audit Report Generation: Interactive Reportable implementation
  formatting full project rosters and statistics ready for faculty review.


5. TECHNOLOGY STACK
--------------------------------------------------------------------------------
• Language & Runtime: Java 17+ (Compatible with OpenJDK 17, 21, 25+)
• GUI Toolkit: Standard Java Swing (javax.swing, java.awt)
• Theme Engine: FlatLaf Light 3.4.1 (Apache 2.0 License)
• Data Storage: Generic In-Memory Repository backed by primitive arrays (Object[])
• Operating System: Windows (Supports macOS and Linux via standard Java commands)
• IDE Target: Visual Studio Code / Eclipse / IntelliJ IDEA / NetBeans


6. APPLICATION ARCHITECTURE
--------------------------------------------------------------------------------
                    +---------------------------+
                    |           USER            |
                    +---------------------------+
                                  |
                                  v
                    +---------------------------+
                    |      SWING UI LAYER       |
                    | JFrame, CardLayout, JTable|
                    | Panels & Custom Renderers |
                    +---------------------------+
                                  |
                                  v
                    +---------------------------+
                    |       SERVICE LAYER       |
                    |   ContributionService     |
                    | Authorization & Validation|
                    +---------------------------+
                                  |
                                  v
                    +---------------------------+
                    |     REPOSITORY LAYER      |
                    |      Repository<T>        |
                    | Array Storage & Resizing  |
                    +---------------------------+
                                  |
                                  v
                    +---------------------------+
                    |        MODEL LAYER        |
                    | Member, StudentMember,    |
                    | TeamLead, Task, Team      |
                    +---------------------------+


7. FOLDER STRUCTURE
--------------------------------------------------------------------------------
SyncSphere/
├── README.txt                      <- Full academic documentation & viva mapping
├── run.bat                         <- Windows batch compile and execution script
├── run.ps1                         <- PowerShell execution script
│
├── lib/
│   ├── flatlaf.jar                 <- FlatLaf 3.4.1 library
│   └── LICENSE.txt                 <- FlatLaf Apache 2.0 license notice
│
├── src/
│   ├── contracts/
│   │   ├── Reportable.java         <- Base reporting interface with constant
│   │   └── Gradable.java           <- Generic interface extending Reportable
│   │
│   ├── exception/
│   │   ├── InvalidContributionException.java  <- Checked exception
│   │   ├── MemberNotFoundException.java       <- Checked exception
│   │   └── DuplicateMemberException.java      <- Unchecked exception
│   │
│   ├── model/
│   │   ├── Member.java             <- Abstract base class for team members
│   │   ├── StudentMember.java      <- Student member concrete subclass
│   │   ├── TeamLead.java           <- Team lead concrete subclass
│   │   ├── Task.java               <- Task entity with lifecycle states
│   │   ├── Contribution.java       <- Individual work hour contribution log
│   │   └── Team.java               <- Team entity with TeamStats inner class
│   │
│   ├── repository/
│   │   └── Repository.java         <- Generic repository backed by Object[]
│   │
│   ├── service/
│   │   └── ContributionService.java<- Core RBAC, validation & business logic
│   │
│   ├── app/
│   │   ├── SyncSphereApp.java      <- Main entry point & JFrame controller
│   │   └── SyncSphereSystemTest.java<- Automated verification test suite
│   │
│   └── ui/
│       ├── UITheme.java            <- Design system, colors, fonts, renderers
│       ├── NavigationCallback.java <- UI event orchestration interface
│       ├── LoginPanel.java         <- First-run account creation & login
│       ├── MainDashboardShell.java <- Header bar, sidebar, and CardLayout shell
│       ├── DashboardPanel.java     <- Dynamic statistical cards and recent tasks
│       ├── MembersPanel.java       <- Team roster & member registration
│       ├── ContributionsPanel.java <- Role-enforced hours logging & history
│       ├── TasksPanel.java         <- Task creation, assignment & completion
│       ├── ApprovalsPanel.java     <- Lead review & approval of completed tasks
│       ├── ReportsPanel.java       <- Team performance audit & report exporter
│       └── ProfilePanel.java       <- Logged-in user profile & standing
│
├── resources/                      <- UI icons and graphics assets
└── out/                            <- Compiled .class bytecode destination


8. EXPLANATION OF ADDITIONAL UI FILES
--------------------------------------------------------------------------------
To adhere to clean software engineering practices and prevent creating an
unmaintainable 3,000-line single file in SyncSphereApp.java, the presentation layer
was modularized into focused Swing panels in the `ui` package:
1. UITheme.java: Centralizes color tokens (Indigo/Slate), custom fonts, rounded
   card borders, and custom TableCellRenderers (Status badge badges).
2. NavigationCallback.java: Eliminates tight coupling between child screens and the
   main shell by providing callback methods for view transitions and logout.
3. LoginPanel.java: Encapsulates both First-Run account initialization and standard
   Member-ID authentication.
4. MainDashboardShell.java: Houses the top navigation header and left sidebar
   while using CardLayout to switch seamlessly between application modules.
5. DashboardPanel, MembersPanel, ContributionsPanel, TasksPanel, ApprovalsPanel,
   ReportsPanel, ProfilePanel: Each represents an isolated functional screen
   responsible for its respective user interactions, keeping event handling clean.


9. CORE JAVA VIVA CONCEPT-TO-FILE MAPPING
--------------------------------------------------------------------------------
+-------------------------------+-----------------------------------+-----------------------------------------------+
| Syllabus Concept              | Primary Java File                 | Implementation Details                        |
+-------------------------------+-----------------------------------+-----------------------------------------------+
| Classes & Objects             | Task.java, Contribution.java      | Concrete state and behavior encapsulation     |
| Constructors & Overloading    | Member.java, Task.java            | Chained constructors using this(...)          |
| 'this' Keyword                | Member.java, Task.java            | Field disambiguation & constructor chaining   |
| Encapsulation & Modifiers     | All Model classes                 | Private fields with public getters/setters    |
| Method Overloading            | Repository.java, Member.java      | Multiple variants of add/remove/constructor   |
| toString(), equals(), hashCode| Member.java, Task.java            | Custom equality by ID, string representations |
| Wrapper Classes               | Member.java, UITheme.java         | Double.valueOf(), Integer parsing             |
| String Manipulation           | ContributionService.java          | trim(), equalsIgnoreCase(), contains()        |
| Arrays & Array Manipulation   | Repository.java, Team.java        | Object[] array buffer with System.arraycopy   |
| Loops, if/else, switch        | ContributionService.java, Team.java Standard control flow in validation/lookup    |
| Meaningful Recursion          | Team.java (TeamStats)             | recursiveSumHours() calculating total hours   |
| Abstract Classes              | Member.java                       | public abstract class Member                  |
| Inheritance & 'super'         | StudentMember.java, TeamLead.java | Extends Member, invokes super(...)            |
| Method Overriding             | StudentMember.java, TeamLead.java | Overrides calculateGrade() & getRoleSpecific  |
| Runtime Polymorphism          | ContributionService.java          | Member base reference pointing to subclasses  |
| Interfaces                    | Reportable.java                   | Interface with static final REPORT_TYPE       |
| Interface Inheritance         | Gradable.java                     | public interface Gradable<T> extends Reportable|
| Generic Classes               | Repository.java                   | public class Repository<T>                    |
| Generic Methods               | Repository.java                   | public static <E> void copy(...), <E> E[] toArray|
| Inner Classes (Non-static)    | Team.java (TeamStats)             | Non-static inner class accessing Team fields  |
| Checked Exceptions            | InvalidContributionException.java | Custom checked exceptions (extends Exception) |
| Checked Exceptions            | MemberNotFoundException.java      | Thrown during missing lookup (throw/throws)   |
| Unchecked Exceptions          | DuplicateMemberException.java     | Unchecked runtime exception (extends Runtime) |
| try-catch-finally             | SyncSphereApp.java, Test Suite    | Robust exception interception & recovery      |
| Casting & instanceof          | Member.java, ContributionService  | (Member) obj and currentActor instanceof Lead |
| Swing GUI & Layout Managers   | SyncSphereApp.java, UI Panels     | JFrame, JPanel, CardLayout, BorderLayout, etc.|
| Event Dispatch Thread (EDT)   | SyncSphereApp.java                | SwingUtilities.invokeLater() safety           |
+-------------------------------+-----------------------------------+-----------------------------------------------+


10. SWING COMPONENTS DEMONSTRATED
--------------------------------------------------------------------------------
• JFrame: The top-level application window (1200x750, resizable, centered).
• JPanel: Modular container panels for cards, sidebar, forms, and headers.
• JLabel: Dynamic headings, metric numbers, and field descriptions.
• JTextField: User input for names, IDs, emails, hours, and task titles.
• JTextArea: Multi-line deliverable descriptions and formal audit report text.
• JComboBox: Dropdown selection for roles (Team Lead/Student) and task assignees.
• JTable & DefaultTableModel: Dynamic data presentation for Members, Tasks,
  Contributions, Approvals, and Academic Audit Reports.
• JScrollPane: Viewport scrolling for tables and extensive report dialogs.
• JOptionPane: Native modal dialogs for validation alerts, confirmations, and reports.
• CardLayout: High-performance view switching without reloading native frames.
• BorderLayout: Structured application shell (Header at NORTH, Sidebar at WEST,
  Content at CENTER).
• GridLayout / GridBagLayout: Aligned stat cards and form fields.


11. FLATLAF INTEGRATION & FALLBACK
--------------------------------------------------------------------------------
SyncSphere incorporates FlatLaf Light to deliver a crisp, modern aesthetic
matching contemporary operating systems.
Fallback Mechanism:
Before initializing Swing components, SyncSphereApp invokes:
    Class.forName("com.formdev.flatlaf.FlatLightLaf").getMethod("setup").invoke(null);
If FlatLaf is absent or fails to load, the catch block intercepts the exception
and automatically configures UIManager.getSystemLookAndFeelClassName() without
crashing the application.


12. FIRST-RUN CREATION & LOGIN FLOW
--------------------------------------------------------------------------------
1. First-Run Account Creation:
   - When the application starts, it checks if memberRepository is empty.
   - If empty, it displays "Create Your First Account".
   - Required fields: Name, Email, Member ID / Roll Number, Role.
   - Validates all fields, adds the user, and automatically logs them in.
2. Standard User Login:
   - On subsequent launches or after logout, shows the Member-ID Login screen.
   - User inputs their unique Roll Number / Member ID.
   - Stored Member object is resolved. The object itself dictates whether the user
     operates with Team Lead or Student privileges.
   - Note on Prototype Authentication: Member-ID authentication is intended for
     classroom demonstration and rapid evaluation. In a production environment,
     salted password hashing (e.g. BCrypt) and session tokens would be utilized.


13. ROLE-BASED ACCESS CONTROL (RBAC) MATRIX
--------------------------------------------------------------------------------
+------------------------------------------+---------------+----------------+
| Operation                                | Team Lead     | Student Member |
+------------------------------------------+---------------+----------------+
| View Personal Dashboard                  | Allowed       | Allowed        |
| Register New Members                     | Allowed       | DENIED         |
| View All Registered Members              | Allowed       | Allowed (view) |
| Create Deliverable Tasks                 | Allowed       | DENIED         |
| Assign / Reassign Tasks                  | Allowed       | DENIED         |
| Log Own Contribution Hours               | Allowed       | Allowed        |
| Log Hours on Behalf of Other Members     | Allowed       | DENIED         |
| View Full Team Contribution History      | Allowed       | DENIED (own)   |
| Mark Assigned Task as Completed          | Allowed       | Allowed (own)  |
| Approve Completed Tasks                  | Allowed       | DENIED         |
| View Full Team Academic Audit Report     | Allowed       | Allowed        |
| Access Approvals Queue Panel             | Visible       | Hidden         |
+------------------------------------------+---------------+----------------+


14. TASK LIFECYCLE
--------------------------------------------------------------------------------
        +------------------------------------------------+
        |                    PENDING                     |
        |  Created by Team Lead, assigned to a member.   |
        +------------------------------------------------+
                               |
                               | Assigned Student finishes work
                               v
        +------------------------------------------------+
        |                   COMPLETED                    |
        |  Submitted by Student; queued for Lead review. |
        +------------------------------------------------+
                               |
                               | Team Lead reviews & approves
                               v
        +------------------------------------------------+
        |                   APPROVED                     |
        |  Officially verified, locked & counted in rate.|
        +------------------------------------------------+


15. EXCEPTION HANDLING STRATEGY
--------------------------------------------------------------------------------
• Checked Exceptions:
  - InvalidContributionException: Thrown when contribution hours are <= 0,
    descriptions are empty, or numerical formatting fails.
  - MemberNotFoundException: Thrown when looking up a member ID that does not exist.
• Unchecked Exceptions:
  - DuplicateMemberException: Thrown if an ID collision is detected during
    member registration.
  - SecurityException: Thrown if a student attempts an unauthorized operation.
  - IllegalStateException: Thrown if illegal task transitions are attempted
    (e.g., approving an uncompleted task, or re-approving an approved task).
All exceptions are safely caught in the UI layer and converted into informative
JOptionPane dialogs to ensure the application never crashes on invalid user input.


16. HOW TO COMPILE AND RUN
--------------------------------------------------------------------------------
PREREQUISITES:
- Windows 10/11
- JDK 17 or newer installed and configured in PATH.

METHOD 1: Using Windows Batch Script (run.bat)
1. Open Command Prompt in the `SyncSphere` directory.
2. Run:
   run.bat

METHOD 2: Using Windows PowerShell Script (run.ps1)
1. Open PowerShell in the `SyncSphere` directory.
2. Run:
   .\run.ps1

METHOD 3: Manual Terminal Compilation and Execution
From inside the `SyncSphere` directory:
1. Compile:
   javac -encoding UTF-8 -cp "lib/*" -d out src/contracts/*.java src/exception/*.java src/model/*.java src/repository/*.java src/service/*.java src/ui/*.java src/app/*.java

2. Run Application:
   java -cp "out;lib/*" app.SyncSphereApp

3. Run Automated System Test Suite:
   java -cp "out;lib/*" app.SyncSphereSystemTest


17. TESTING VERIFICATION WORKFLOW
--------------------------------------------------------------------------------
The application was rigorously verified using both GUI interactive testing and
the automated SyncSphereSystemTest suite:
1. Start application with empty repository.
2. Create initial Team Lead (Alex Morgan, TL-001).
3. Register Student A (Bob Smith, STU-101) and Student B (Charlie Davis, STU-102).
4. Attempt duplicate registration of STU-101 -> DuplicateMemberException caught.
5. Team Lead logs initial 12.0 hours.
6. Team Lead creates TSK-101 assigned to Bob Smith and TSK-102 assigned to Charlie Davis.
7. Logout and login as Student A (STU-101).
8. Attempt member registration as Student -> Access Denied dialog.
9. Attempt logging hours for Student B -> Access Denied dialog.
10. Attempt negative hours (-3.5) -> InvalidContributionException caught.
11. Log valid 14.5 hours for self.
12. Mark TSK-101 as Completed.
13. Attempt to approve TSK-101 as Student -> Access Denied dialog.
14. Logout and login as Team Lead (TL-001).
15. Navigate to Approvals queue and approve TSK-101.
16. Attempt repeated approval -> IllegalStateException caught.
17. Open Team Reports: Verify recursive team total is exactly 26.5 hours and
    Top Contributor is Bob Smith.
18. Logout and login as Student B: Verify user isolation (0 hours, 0 logs, 1 task).
Result: 23/23 Automated System Tests Passed (100%).


18. KNOWN LIMITATIONS
--------------------------------------------------------------------------------
• In-Memory Storage: In accordance with project restrictions (no JDBC, SQLite, or
  external DBs), application data is stored in memory for the duration of the
  JVM session. Closing the window resets data to clean initial state.
• Plaintext ID Login: Roll number login is designed for rapid academic demo.
  Production systems would integrate salted password credentials.


19. FUTURE IMPROVEMENTS
--------------------------------------------------------------------------------
• Serialization / File I/O: Implement ObjectOutputStream or JSON file persistence
  to retain team history across app restarts without adding database dependencies.
• Deliverable Attachments: Allow students to attach file links or commit hashes
  directly to task logs.
• Dark Mode Toggle: Integrate FlatLaf Dark theme switcher in the header bar.
================================================================================
