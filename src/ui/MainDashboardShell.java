package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import model.Member;
import service.ContributionService;

/**
 * Main Application Shell.
 * Implements Section 5 (Main Layout), Section 9 (RBAC), and Section 18 (Logout).
 * Uses BorderLayout for the shell, CardLayout for screens, and a sleek sidebar.
 */
public class MainDashboardShell extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    // Header Components
    private JLabel lblUserGreeting;
    private JButton btnLogout;

    // Sidebar & Navigation
    private JPanel sidebarPanel;
    private Map<String, JButton> navButtons;
    private String activeView = "DASHBOARD";

    private JButton btnApprovals;
    private JButton btnMembers;

    // Content Cards
    private JPanel centerCardContainer;
    private CardLayout cardLayout;

    private DashboardPanel dashboardPanel;
    private MembersPanel membersPanel;
    private ContributionsPanel contributionsPanel;
    private TasksPanel tasksPanel;
    private ApprovalsPanel approvalsPanel;
    private ReportsPanel reportsPanel;
    private ProfilePanel profilePanel;

    public MainDashboardShell(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;
        this.navButtons = new HashMap<String, JButton>();

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_BG);

        // Build Top Bar
        add(buildHeaderBar(), BorderLayout.NORTH);

        // Build Sidebar
        add(buildSidebar(), BorderLayout.WEST);

        // Build Center Card Area
        cardLayout = new CardLayout();
        centerCardContainer = new JPanel(cardLayout);
        centerCardContainer.setOpaque(false);

        // Instantiate subpanels
        dashboardPanel = new DashboardPanel(service, callback);
        membersPanel = new MembersPanel(service, callback);
        contributionsPanel = new ContributionsPanel(service, callback);
        tasksPanel = new TasksPanel(service, callback);
        approvalsPanel = new ApprovalsPanel(service, callback);
        reportsPanel = new ReportsPanel(service, callback);
        profilePanel = new ProfilePanel(service, callback);

        centerCardContainer.add(dashboardPanel, "DASHBOARD");
        centerCardContainer.add(membersPanel, "MEMBERS");
        centerCardContainer.add(contributionsPanel, "CONTRIBUTIONS");
        centerCardContainer.add(tasksPanel, "TASKS");
        centerCardContainer.add(approvalsPanel, "APPROVALS");
        centerCardContainer.add(reportsPanel, "REPORTS");
        centerCardContainer.add(profilePanel, "PROFILE");

        add(centerCardContainer, BorderLayout.CENTER);
    }

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, UITheme.COLOR_BORDER));
        header.setPreferredSize(new Dimension(1200, 64));

        // Brand Label
        JPanel brandBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        brandBlock.setOpaque(false);

        JLabel brandName = new JLabel("SyncSphere");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandName.setForeground(UITheme.COLOR_PRIMARY);

        JLabel brandTag = new JLabel("|  Team Contribution & Task Management");
        brandTag.setFont(UITheme.FONT_SUBTITLE);
        brandTag.setForeground(UITheme.COLOR_TEXT_MUTED);

        brandBlock.add(brandName);
        brandBlock.add(brandTag);
        header.add(brandBlock, BorderLayout.WEST);

        // User Info & Logout
        JPanel userBlock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        userBlock.setOpaque(false);

        lblUserGreeting = new JLabel("Welcome, User");
        lblUserGreeting.setFont(UITheme.FONT_BOLD);
        lblUserGreeting.setForeground(UITheme.COLOR_TEXT_MAIN);

        btnLogout = UITheme.createSecondaryButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        userBlock.add(lblUserGreeting);
        userBlock.add(btnLogout);
        header.add(userBlock, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UITheme.COLOR_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(220, 680));
        sidebarPanel.setBorder(new EmptyBorder(16, 12, 16, 12));

        JLabel menuTitle = new JLabel("NAVIGATION");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuTitle.setForeground(new Color(148, 163, 184)); // Slate 400
        menuTitle.setBorder(new EmptyBorder(6, 12, 8, 12));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(menuTitle);

        addNavButton("Dashboard", "DASHBOARD");
        btnMembers = addNavButton("Members", "MEMBERS");
        addNavButton("Contributions", "CONTRIBUTIONS");
        addNavButton("Tasks", "TASKS");
        btnApprovals = addNavButton("Approvals", "APPROVALS");
        addNavButton("Reports", "REPORTS");
        addNavButton("My Profile", "PROFILE");

        sidebarPanel.add(Box.createVerticalGlue());

        // System info banner at bottom of sidebar
        JLabel sysInfo = new JLabel("Core Java CBP 2026");
        sysInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sysInfo.setForeground(new Color(100, 116, 139));
        sysInfo.setBorder(new EmptyBorder(0, 12, 4, 12));
        sysInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sysInfo);

        return sidebarPanel;
    }

    private JButton addNavButton(String title, final String viewKey) {
        final JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(UITheme.COLOR_SIDEBAR_TEXT);
        btn.setBackground(UITheme.COLOR_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView(viewKey);
            }
        });

        navButtons.put(viewKey, btn);
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(4));
        return btn;
    }

    public void switchView(String viewKey) {
        this.activeView = viewKey;
        cardLayout.show(centerCardContainer, viewKey);

        // Update button styles
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equalsIgnoreCase(viewKey)) {
                btn.setBackground(UITheme.COLOR_PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                btn.setBackground(UITheme.COLOR_SIDEBAR);
                btn.setForeground(UITheme.COLOR_SIDEBAR_TEXT);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        }
    }

    public void setUser(Member user) {
        this.currentUser = user;
        if (currentUser == null) return;

        lblUserGreeting.setText("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ") | ID: " + currentUser.getId());

        // Role-based navigation visibility
        boolean isLead = currentUser.canManageTeam();
        btnApprovals.setVisible(isLead);

        // Pass user down to all panels
        dashboardPanel.setCurrentUser(currentUser);
        membersPanel.setCurrentUser(currentUser);
        contributionsPanel.setCurrentUser(currentUser);
        tasksPanel.setCurrentUser(currentUser);
        approvalsPanel.setCurrentUser(currentUser);
        reportsPanel.setCurrentUser(currentUser);
        profilePanel.setCurrentUser(currentUser);

        switchView("DASHBOARD");
    }

    public void refreshAll() {
        if (currentUser == null) return;
        dashboardPanel.refreshData();
        membersPanel.refreshData();
        contributionsPanel.refreshData();
        tasksPanel.refreshData();
        approvalsPanel.refreshData();
        reportsPanel.refreshData();
        profilePanel.refreshData();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of SyncSphere?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            callback.onLogout();
        }
    }
}
