package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Member;
import model.Task;
import repository.Repository;
import service.ContributionService;

/**
 * Approvals Panel (Section 15).
 * Team Lead-only screen to review and approve tasks marked COMPLETED.
 */
public class ApprovalsPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    private JLabel lblHeading;
    private JLabel lblSubheading;
    private JTable approvalTable;
    private DefaultTableModel tableModel;
    private JButton btnApprove;
    private JPanel emptyStatePanel;
    private JScrollPane scrollPane;

    public ApprovalsPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblHeading = new JLabel("Task Approvals & Quality Assurance");
        lblHeading.setFont(UITheme.FONT_TITLE);
        lblHeading.setForeground(UITheme.COLOR_TEXT_MAIN);

        lblSubheading = new JLabel("Team Lead exclusive review queue for submitted deliverable tasks");
        lblSubheading.setFont(UITheme.FONT_SUBTITLE);
        lblSubheading.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblHeading, BorderLayout.NORTH);
        headerPanel.add(lblSubheading, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 14));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel sectionTitle = new JLabel("Deliverables Awaiting Approval");
        sectionTitle.setFont(UITheme.FONT_HEADER);
        topBar.add(sectionTitle, BorderLayout.WEST);

        btnApprove = UITheme.createPrimaryButton("Approve Selected Task");
        btnApprove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleApproveTask();
            }
        });
        topBar.add(btnApprove, BorderLayout.EAST);

        card.add(topBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Task ID", "Title", "Description", "Submitted By", "Current Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        approvalTable = new JTable(tableModel);
        UITheme.styleTable(approvalTable);
        approvalTable.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        scrollPane = new JScrollPane(approvalTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Empty state label
        emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setOpaque(false);
        JLabel lblEmpty = new JLabel("All caught up! No tasks are currently awaiting approval.");
        lblEmpty.setFont(UITheme.FONT_HEADER);
        lblEmpty.setForeground(UITheme.COLOR_TEXT_MUTED);
        emptyStatePanel.add(lblEmpty);

        card.add(scrollPane, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    public void setCurrentUser(Member user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        if (currentUser == null) return;

        boolean isLead = currentUser.canManageTeam();
        btnApprove.setEnabled(isLead);

        if (!isLead) {
            lblHeading.setText("Task Approvals (Access Denied)");
            lblSubheading.setText("Only Team Lead can review and approve deliverable tasks.");
            tableModel.setRowCount(0);
            return;
        }

        lblHeading.setText("Task Approvals & Quality Assurance");
        lblSubheading.setText("Team Lead exclusive review queue for submitted deliverable tasks");

        tableModel.setRowCount(0);
        Repository<Task> awaiting = service.getTasksAwaitingApproval();

        for (int i = 0; i < awaiting.size(); i++) {
            Task t = awaiting.get(i);
            tableModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getTitle(),
                    t.getDescription(),
                    t.getAssignedMemberName() + " (" + t.getAssignedMemberId() + ")",
                    t.getStatus()
            });
        }
    }

    private void handleApproveTask() {
        if (currentUser == null || !currentUser.canManageTeam()) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied!\nOnly Team Lead can perform this operation.",
                    "Unauthorized Action", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = approvalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a completed task from the table to approve.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String taskId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        if (!Task.STATUS_COMPLETED.equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Only tasks in COMPLETED status can be approved.\nTask current status: " + currentStatus,
                    "Invalid State", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to approve Task '" + taskId + "'?\nThis marks the work officially approved and closed.",
                "Confirm Approval", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.approveTask(currentUser, taskId);
                JOptionPane.showMessageDialog(this,
                        "Task '" + taskId + "' has been APPROVED successfully!",
                        "Approval Confirmed", JOptionPane.INFORMATION_MESSAGE);
                callback.refreshAllViews();
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "State Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Approval failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
