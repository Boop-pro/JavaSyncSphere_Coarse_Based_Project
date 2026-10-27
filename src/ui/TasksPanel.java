package ui;

import exception.MemberNotFoundException;
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
 * Task Management Panel (Section 14).
 * Handles task creation, assignment, tracking, and student completion transitions.
 */
public class TasksPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    // Creation Form (Lead only)
    private JPanel createCard;
    private JTextField txtTitle;
    private JTextField txtDesc;
    private JComboBox<String> cmbAssignee;
    private JButton btnCreateTask;

    // Table & Actions
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private JButton btnMarkCompleted;
    private JButton btnReassign;

    public TasksPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Task Allocation & Lifecycle Tracking");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_TEXT_MAIN);

        JLabel lblDesc = new JLabel("Manage deliverables through lifecycle: PENDING \u2192 COMPLETED \u2192 APPROVED");
        lblDesc.setFont(UITheme.FONT_SUBTITLE);
        lblDesc.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblDesc, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center container
        JPanel centerPanel = new JPanel(new BorderLayout(0, 18));
        centerPanel.setOpaque(false);

        // Task Creation Card (Lead only)
        createCard = UITheme.createCardPanel();
        createCard.setLayout(new BorderLayout(0, 14));

        JLabel createHeader = new JLabel("Create New Deliverable Task");
        createHeader.setFont(UITheme.FONT_HEADER);
        createHeader.setForeground(UITheme.COLOR_PRIMARY);
        createCard.add(createHeader, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(2, 3, 14, 10));
        formGrid.setOpaque(false);

        formGrid.add(new JLabel("Task Title / Objective:"));
        txtTitle = new JTextField();
        formGrid.add(txtTitle);

        formGrid.add(new JLabel("Deliverable Description:"));
        txtDesc = new JTextField();
        formGrid.add(txtDesc);

        formGrid.add(new JLabel("Assign To:"));
        cmbAssignee = new JComboBox<String>();
        formGrid.add(cmbAssignee);

        createCard.add(formGrid, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBar.setOpaque(false);
        btnCreateTask = UITheme.createPrimaryButton("Create Task");
        btnCreateTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateTask();
            }
        });
        btnBar.add(btnCreateTask);
        createCard.add(btnBar, BorderLayout.SOUTH);

        centerPanel.add(createCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);

        JLabel tblTitle = new JLabel("Project Tasks Board");
        tblTitle.setFont(UITheme.FONT_HEADER);
        tableTop.add(tblTitle, BorderLayout.WEST);

        // Action Buttons Bar
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsBar.setOpaque(false);

        btnMarkCompleted = UITheme.createPrimaryButton("Mark as Completed");
        btnMarkCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMarkCompleted();
            }
        });

        btnReassign = UITheme.createSecondaryButton("Reassign Task");
        btnReassign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReassignTask();
            }
        });

        actionsBar.add(btnMarkCompleted);
        actionsBar.add(btnReassign);
        tableTop.add(actionsBar, BorderLayout.EAST);

        tableCard.add(tableTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Task ID", "Title", "Description", "Assigned Member", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tasksTable = new JTable(tableModel);
        UITheme.styleTable(tasksTable);
        tasksTable.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void setCurrentUser(Member user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        if (currentUser == null) return;

        boolean isLead = currentUser.canManageTeam();
        createCard.setVisible(isLead);
        btnReassign.setVisible(isLead);

        // Populate Assignee dropdown
        cmbAssignee.removeAllItems();
        cmbAssignee.addItem("UNASSIGNED - (Leave Unassigned)");
        Repository<Member> members = service.getMemberRepository();
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            cmbAssignee.addItem(m.getId() + " - " + m.getName() + " (" + m.getRole() + ")");
        }

        // Populate Tasks Table
        tableModel.setRowCount(0);
        Repository<Task> tasks = service.getTaskRepository();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            tableModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getTitle(),
                    t.getDescription(),
                    t.getAssignedMemberName() + " (" + t.getAssignedMemberId() + ")",
                    t.getStatus()
            });
        }
    }

    private void handleCreateTask() {
        if (currentUser == null || !currentUser.canManageTeam()) {
            JOptionPane.showMessageDialog(this, "Access Denied!\nOnly Team Lead can create tasks.", "Unauthorized Action", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = txtTitle.getText();
        String desc = txtDesc.getText();
        String assignee = (String) cmbAssignee.getSelectedItem();

        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task Title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String targetMemberId = "UNASSIGNED";
        if (assignee != null && !assignee.startsWith("UNASSIGNED")) {
            targetMemberId = assignee.split(" - ")[0].trim();
        }

        try {
            Task task = service.createTask(currentUser, title, desc, targetMemberId);
            JOptionPane.showMessageDialog(this,
                    "Task '" + task.getTaskId() + "' created successfully with status PENDING!",
                    "Task Created", JOptionPane.INFORMATION_MESSAGE);

            txtTitle.setText("");
            txtDesc.setText("");
            cmbAssignee.setSelectedIndex(0);

            callback.refreshAllViews();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleMarkCompleted() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task from the table to complete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String taskId = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        if (!Task.STATUS_PENDING.equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only tasks in PENDING status can be marked as COMPLETED.\nCurrent status: " + status, "Invalid State Transition", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            service.completeTask(currentUser, taskId);
            JOptionPane.showMessageDialog(this,
                    "Task '" + taskId + "' successfully marked as COMPLETED!\nIt is now queued for Team Lead approval.",
                    "Task Completed", JOptionPane.INFORMATION_MESSAGE);
            callback.refreshAllViews();
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "State Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReassignTask() {
        if (currentUser == null || !currentUser.canManageTeam()) {
            JOptionPane.showMessageDialog(this, "Access Denied!\nOnly Team Lead can assign or reassign tasks.", "Unauthorized Action", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to reassign.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String taskId = (String) tableModel.getValueAt(selectedRow, 0);
        Repository<Member> members = service.getMemberRepository();
        String[] options = new String[members.size() + 1];
        options[0] = "UNASSIGNED";
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            options[i + 1] = m.getId() + " - " + m.getName();
        }

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select new assignee for Task " + taskId + ":",
                "Reassign Task",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (chosen != null) {
            String newAssigneeId = chosen.startsWith("UNASSIGNED") ? "UNASSIGNED" : chosen.split(" - ")[0].trim();
            try {
                service.assignTask(currentUser, taskId, newAssigneeId);
                JOptionPane.showMessageDialog(this, "Task '" + taskId + "' reassigned to: " + chosen, "Task Reassigned", JOptionPane.INFORMATION_MESSAGE);
                callback.refreshAllViews();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
