package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Member;
import model.StudentMember;
import model.Task;
import model.Team;
import repository.Repository;
import service.ContributionService;

/**
 * Dashboard Panel displaying dynamic statistics and task overview.
 * Implements Section 10 (Team Lead Dashboard) and Section 11 (Student Dashboard).
 */
public class DashboardPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    private JPanel statsContainer;
    private JLabel lblWelcomeHeading;
    private JLabel lblSubheading;

    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private JButton btnQuickComplete;

    public DashboardPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblWelcomeHeading = new JLabel("Dashboard Overview");
        lblWelcomeHeading.setFont(UITheme.FONT_TITLE);
        lblWelcomeHeading.setForeground(UITheme.COLOR_TEXT_MAIN);

        lblSubheading = new JLabel("Live project activity and contribution metrics");
        lblSubheading.setFont(UITheme.FONT_SUBTITLE);
        lblSubheading.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblWelcomeHeading, BorderLayout.NORTH);
        headerPanel.add(lblSubheading, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 18));
        centerPanel.setOpaque(false);

        statsContainer = new JPanel();
        statsContainer.setOpaque(false);
        centerPanel.add(statsContainer, BorderLayout.NORTH);

        // Table Section
        JPanel tableSection = UITheme.createCardPanel();
        tableSection.setLayout(new BorderLayout(0, 12));

        JPanel tableHeaderBar = new JPanel(new BorderLayout());
        tableHeaderBar.setOpaque(false);

        JLabel tableTitle = new JLabel("Recent Project Tasks");
        tableTitle.setFont(UITheme.FONT_HEADER);
        tableTitle.setForeground(UITheme.COLOR_TEXT_MAIN);
        tableHeaderBar.add(tableTitle, BorderLayout.WEST);

        btnQuickComplete = UITheme.createPrimaryButton("Mark Task as Completed");
        btnQuickComplete.setVisible(false); // only for student
        btnQuickComplete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleQuickComplete();
            }
        });
        tableHeaderBar.add(btnQuickComplete, BorderLayout.EAST);

        tableSection.add(tableHeaderBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Task ID", "Title", "Assigned To", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tasksTable = new JTable(tableModel);
        UITheme.styleTable(tasksTable);
        tasksTable.getColumnModel().getColumn(3).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableSection.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableSection, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void setCurrentUser(Member user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        if (currentUser == null) return;

        lblWelcomeHeading.setText("Welcome back, " + currentUser.getName() + "!");
        lblSubheading.setText("Role: " + currentUser.getRole() + " | ID: " + currentUser.getId() + " | Performance Grade: " + currentUser.calculateGrade());

        statsContainer.removeAll();

        if (currentUser.canManageTeam()) {
            buildTeamLeadStats();
            btnQuickComplete.setVisible(false);
            populateTeamLeadTasks();
        } else {
            buildStudentStats();
            btnQuickComplete.setVisible(true);
            populateStudentTasks();
        }

        statsContainer.revalidate();
        statsContainer.repaint();
    }

    private void buildTeamLeadStats() {
        statsContainer.setLayout(new GridLayout(2, 3, 14, 14));
        Team.TeamStats stats = service.getTeam().getStats();

        statsContainer.add(UITheme.createStatCard("Total Members", String.valueOf(stats.getTotalMembers()), UITheme.COLOR_PRIMARY));
        statsContainer.add(UITheme.createStatCard("Total Tasks", String.valueOf(stats.getTotalTasks()), UITheme.COLOR_ACCENT));
        statsContainer.add(UITheme.createStatCard("Total Team Hours", String.format("%.1f hrs", stats.calculateTotalHours()), new Color(13, 148, 136))); // Teal
        statsContainer.add(UITheme.createStatCard("Pending Tasks", String.valueOf(stats.getPendingTasksCount()), new Color(217, 119, 6))); // Amber
        statsContainer.add(UITheme.createStatCard("Awaiting Approval", String.valueOf(stats.getCompletedTasksCount()), new Color(37, 99, 235))); // Blue
        statsContainer.add(UITheme.createStatCard("Approved Tasks", String.valueOf(stats.getApprovedTasksCount()), new Color(5, 150, 105))); // Emerald
    }

    private void buildStudentStats() {
        statsContainer.setLayout(new GridLayout(1, 5, 12, 12));

        Repository<Task> myTasks = service.getTasksForMember(currentUser.getId());
        int pending = 0;
        int completed = 0;
        int approved = 0;

        for (int i = 0; i < myTasks.size(); i++) {
            Task t = myTasks.get(i);
            if (Task.STATUS_PENDING.equalsIgnoreCase(t.getStatus())) pending++;
            else if (Task.STATUS_COMPLETED.equalsIgnoreCase(t.getStatus())) completed++;
            else if (Task.STATUS_APPROVED.equalsIgnoreCase(t.getStatus())) approved++;
        }

        statsContainer.add(UITheme.createStatCard("My Contribution Hours", String.format("%.1f hrs", currentUser.getTotalHours()), UITheme.COLOR_PRIMARY));
        statsContainer.add(UITheme.createStatCard("My Assigned Tasks", String.valueOf(myTasks.size()), UITheme.COLOR_ACCENT));
        statsContainer.add(UITheme.createStatCard("My Pending Tasks", String.valueOf(pending), new Color(217, 119, 6)));
        statsContainer.add(UITheme.createStatCard("My Completed Tasks", String.valueOf(completed), new Color(37, 99, 235)));
        statsContainer.add(UITheme.createStatCard("My Approved Tasks", String.valueOf(approved), new Color(5, 150, 105)));
    }

    private void populateTeamLeadTasks() {
        tableModel.setRowCount(0);
        Repository<Task> tasks = service.getTaskRepository();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            tableModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getTitle(),
                    t.getAssignedMemberName() + " (" + t.getAssignedMemberId() + ")",
                    t.getStatus()
            });
        }
    }

    private void populateStudentTasks() {
        tableModel.setRowCount(0);
        Repository<Task> myTasks = service.getTasksForMember(currentUser.getId());
        for (int i = 0; i < myTasks.size(); i++) {
            Task t = myTasks.get(i);
            tableModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getTitle(),
                    "Assigned to Me",
                    t.getStatus()
            });
        }
    }

    private void handleQuickComplete() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assigned task to mark as completed.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String taskId = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 3);

        if (!Task.STATUS_PENDING.equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only tasks in PENDING status can be completed.\nSelected task is already: " + status, "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark task '" + taskId + "' as Completed?\nThis will submit it to the Team Lead for final approval.",
                "Confirm Task Completion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.completeTask(currentUser, taskId);
                JOptionPane.showMessageDialog(this, "Task '" + taskId + "' marked as COMPLETED successfully!\nAwaiting Team Lead approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
                callback.refreshAllViews();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
