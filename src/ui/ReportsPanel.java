package ui;

import contracts.Reportable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Member;
import model.Task;
import model.Team;
import repository.Repository;
import service.ContributionService;

/**
 * Team Report and Academic Evaluation Panel (Section 16).
 * Demonstrates:
 * - Inner class calculations via Team.TeamStats
 * - Recursive calculation of team hours
 * - Polymorphic grade calculations via Gradable<T>
 * - Interface execution via Reportable.generateReport()
 */
public class ReportsPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    private JPanel statsHeaderGrid;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JButton btnGenerateReport;

    public ReportsPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Team Performance & Academic Audit Report");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_TEXT_MAIN);

        JLabel lblDesc = new JLabel("Comprehensive project contribution summary, recursive hour totals, and performance grading");
        lblDesc.setFont(UITheme.FONT_SUBTITLE);
        lblDesc.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblDesc, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center container
        JPanel centerContainer = new JPanel(new BorderLayout(0, 18));
        centerContainer.setOpaque(false);

        // Stats Header Grid
        statsHeaderGrid = new JPanel(new GridLayout(1, 4, 14, 14));
        statsHeaderGrid.setOpaque(false);
        centerContainer.add(statsHeaderGrid, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));

        JPanel tableTopBar = new JPanel(new BorderLayout());
        tableTopBar.setOpaque(false);

        JLabel tblHeading = new JLabel("Member Contribution & Task Breakdown");
        tblHeading.setFont(UITheme.FONT_HEADER);
        tableTopBar.add(tblHeading, BorderLayout.WEST);

        btnGenerateReport = UITheme.createPrimaryButton("Generate & Print Report (" + Reportable.REPORT_TYPE + ")");
        btnGenerateReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGenerateReport();
            }
        });
        tableTopBar.add(btnGenerateReport, BorderLayout.EAST);

        tableCard.add(tableTopBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "Member ID", "Name", "Role", "Hours", "Grade", "Assigned", "Completed", "Approved"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        UITheme.styleTable(reportTable);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerContainer.add(tableCard, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);
    }

    public void setCurrentUser(Member user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        if (currentUser == null) return;

        Team team = service.getTeam();
        Team.TeamStats stats = team.getStats();

        // Refresh stats cards
        statsHeaderGrid.removeAll();
        double totalHours = stats.calculateTotalHours();
        Member top = stats.getTopContributor();

        String topContributorStr = (top != null) ? top.getName() + " (" + String.format("%.1f", top.getTotalHours()) + " hrs)" : "None";

        statsHeaderGrid.add(UITheme.createStatCard("Total Team Hours (Recursive)", String.format("%.1f hrs", totalHours), UITheme.COLOR_PRIMARY));
        statsHeaderGrid.add(UITheme.createStatCard("Top Contributor", topContributorStr, new Color(13, 148, 136)));
        statsHeaderGrid.add(UITheme.createStatCard("Total Deliverables", String.valueOf(stats.getTotalTasks()), UITheme.COLOR_ACCENT));
        statsHeaderGrid.add(UITheme.createStatCard("Completion Rate", String.format("%.1f%%", stats.getTaskCompletionRate()), new Color(5, 150, 105)));

        statsHeaderGrid.revalidate();
        statsHeaderGrid.repaint();

        // Populate table
        tableModel.setRowCount(0);
        Repository<Member> members = service.getMemberRepository();

        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);

            // Compute task counts for member
            Repository<Task> tasksForM = service.getTasksForMember(m.getId());
            int assignedCount = tasksForM.size();
            int completedCount = 0;
            int approvedCount = 0;

            for (int j = 0; j < tasksForM.size(); j++) {
                Task t = tasksForM.get(j);
                if (Task.STATUS_COMPLETED.equalsIgnoreCase(t.getStatus())) completedCount++;
                else if (Task.STATUS_APPROVED.equalsIgnoreCase(t.getStatus())) approvedCount++;
            }

            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getRole(),
                    String.format("%.1f hrs", m.getTotalHours()),
                    m.calculateGrade(),
                    assignedCount,
                    completedCount,
                    approvedCount
            });
        }
    }

    private void handleGenerateReport() {
        Team team = service.getTeam();
        team.generateReport(); // Invokes Reportable.generateReport()

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("             SYNCSHPERE - " + Reportable.REPORT_TYPE + "\n");
        sb.append("====================================================\n\n");

        Team.TeamStats stats = team.getStats();
        sb.append("Team Name:         ").append(team.getTeamName()).append("\n");
        sb.append("Generated By:      ").append(currentUser.getName()).append(" (").append(currentUser.getRole()).append(")\n");
        sb.append("Total Team Hours:  ").append(String.format("%.1f hrs", stats.calculateTotalHours())).append(" (Calculated Recursively)\n");
        Member top = stats.getTopContributor();
        sb.append("Top Contributor:   ").append(top != null ? top.getName() + " (" + top.getTotalHours() + " hrs)" : "None").append("\n");
        sb.append("Total Tasks:       ").append(stats.getTotalTasks()).append("\n");
        sb.append(" - Pending:        ").append(stats.getPendingTasksCount()).append("\n");
        sb.append(" - Awaiting Appr:  ").append(stats.getCompletedTasksCount()).append("\n");
        sb.append(" - Approved:       ").append(stats.getApprovedTasksCount()).append("\n");
        sb.append("Task Completion:   ").append(String.format("%.1f%%", stats.getTaskCompletionRate())).append("\n\n");
        sb.append("----------------------------------------------------\n");
        sb.append("MEMBER BREAKDOWN:\n");
        sb.append("----------------------------------------------------\n");

        Repository<Member> members = service.getMemberRepository();
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            sb.append(String.format("• [%s] %-15s | %-14s | %5.1f hrs | Grade: %s\n",
                    m.getId(), m.getName(), m.getRole(), m.getTotalHours(), m.calculateGrade()));
        }
        sb.append("====================================================\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(textArea);
        sp.setPreferredSize(new Dimension(550, 400));

        JOptionPane.showMessageDialog(this, sp, "Official Project Audit Report", JOptionPane.INFORMATION_MESSAGE);
    }
}
