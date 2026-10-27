package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Member;
import model.Task;
import repository.Repository;
import service.ContributionService;

/**
 * My Profile Panel (Section 17).
 * Displays personal member profile, role privileges, and assigned task metrics.
 */
public class ProfilePanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    private JLabel lblName;
    private JLabel lblRoleBadge;
    private JLabel lblEmail;
    private JLabel lblId;
    private JLabel lblHours;
    private JLabel lblGrade;
    private JLabel lblAssignedTasks;
    private JLabel lblCompletedTasks;
    private JLabel lblRoleDetails;

    public ProfilePanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("My Profile & Academic Credentials");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_TEXT_MAIN);

        JLabel lblDesc = new JLabel("View personal standing, verified contribution record, and role privileges");
        lblDesc.setFont(UITheme.FONT_SUBTITLE);
        lblDesc.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblDesc, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Profile Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 20));

        // Top avatar/identity block
        JPanel topIdentity = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        topIdentity.setOpaque(false);

        // Circle avatar representation
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                String initial = (currentUser != null && !currentUser.getName().isEmpty())
                        ? currentUser.getName().substring(0, 1).toUpperCase() : "U";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(68, 68));
        avatar.setOpaque(false);
        topIdentity.add(avatar);

        JPanel nameBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        nameBlock.setOpaque(false);
        lblName = new JLabel("User Name");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblName.setForeground(UITheme.COLOR_TEXT_MAIN);

        lblRoleBadge = new JLabel("Student Member");
        lblRoleBadge.setFont(UITheme.FONT_BADGE);
        lblRoleBadge.setForeground(UITheme.COLOR_PRIMARY);

        nameBlock.add(lblName);
        nameBlock.add(lblRoleBadge);
        topIdentity.add(nameBlock);

        card.add(topIdentity, BorderLayout.NORTH);

        // Details grid
        JPanel detailsGrid = new JPanel(new GridLayout(4, 2, 18, 14));
        detailsGrid.setOpaque(false);
        detailsGrid.setBorder(new EmptyBorder(12, 12, 12, 12));

        detailsGrid.add(createFieldBlock("Member ID / Roll Number:", lblId = new JLabel("-")));
        detailsGrid.add(createFieldBlock("Registered Email:", lblEmail = new JLabel("-")));
        detailsGrid.add(createFieldBlock("Total Contribution Hours:", lblHours = new JLabel("-")));
        detailsGrid.add(createFieldBlock("Academic Evaluation Grade:", lblGrade = new JLabel("-")));
        detailsGrid.add(createFieldBlock("Assigned Deliverable Tasks:", lblAssignedTasks = new JLabel("-")));
        detailsGrid.add(createFieldBlock("Completed Tasks:", lblCompletedTasks = new JLabel("-")));

        card.add(detailsGrid, BorderLayout.CENTER);

        // Bottom role details
        JPanel bottomDetails = new JPanel(new BorderLayout());
        bottomDetails.setOpaque(false);
        bottomDetails.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblSpecTitle = new JLabel("Role Privileges & Specific Details:");
        lblSpecTitle.setFont(UITheme.FONT_BOLD);
        lblRoleDetails = new JLabel("-");
        lblRoleDetails.setFont(UITheme.FONT_REGULAR);
        lblRoleDetails.setForeground(UITheme.COLOR_TEXT_MUTED);

        bottomDetails.add(lblSpecTitle, BorderLayout.NORTH);
        bottomDetails.add(lblRoleDetails, BorderLayout.SOUTH);

        card.add(bottomDetails, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private JPanel createFieldBlock(String label, JLabel valueLabel) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_STAT_LABEL);
        lbl.setForeground(UITheme.COLOR_TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_HEADER);
        valueLabel.setForeground(UITheme.COLOR_TEXT_MAIN);

        block.add(lbl, BorderLayout.NORTH);
        block.add(valueLabel, BorderLayout.SOUTH);
        return block;
    }

    public void setCurrentUser(Member user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        if (currentUser == null) return;

        lblName.setText(currentUser.getName());
        lblRoleBadge.setText(currentUser.getRole().toUpperCase());
        lblId.setText(currentUser.getId());
        lblEmail.setText(currentUser.getEmail());
        lblHours.setText(String.format("%.1f Hours Logged", currentUser.getTotalHours()));
        lblGrade.setText(currentUser.calculateGrade());

        Repository<Task> myTasks = service.getTasksForMember(currentUser.getId());
        int assigned = myTasks.size();
        int completed = 0;
        for (int i = 0; i < myTasks.size(); i++) {
            Task t = myTasks.get(i);
            if (Task.STATUS_COMPLETED.equalsIgnoreCase(t.getStatus()) || Task.STATUS_APPROVED.equalsIgnoreCase(t.getStatus())) {
                completed++;
            }
        }

        lblAssignedTasks.setText(String.valueOf(assigned) + " Tasks");
        lblCompletedTasks.setText(String.valueOf(completed) + " Tasks Finished");
        lblRoleDetails.setText(currentUser.getRoleSpecificDetails());

        repaint();
    }
}
