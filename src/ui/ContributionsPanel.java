package ui;

import exception.InvalidContributionException;
import exception.MemberNotFoundException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Contribution;
import model.Member;
import repository.Repository;
import service.ContributionService;

/**
 * Contribution Management Panel (Section 13).
 * Supports role-tailored submission and filtered history view.
 */
public class ContributionsPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    // Form Components
    private JPanel memberSelectPanel;
    private JComboBox<String> cmbMembers;
    private JLabel lblFixedMember;
    private JTextField txtHours;
    private JTextField txtDescription;
    private JButton btnSubmit;

    // History Table
    private JTable contribTable;
    private DefaultTableModel tableModel;

    public ContributionsPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Contribution Hours & Activity Logging");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_TEXT_MAIN);

        JLabel lblDesc = new JLabel("Log verified work hours, task milestones, and track productivity records");
        lblDesc.setFont(UITheme.FONT_SUBTITLE);
        lblDesc.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblDesc, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Split (Top Form, Bottom Table)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 18));
        centerPanel.setOpaque(false);

        // Form Card
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 14));

        JLabel formTitle = new JLabel("Log New Contribution Entry");
        formTitle.setFont(UITheme.FONT_HEADER);
        formTitle.setForeground(UITheme.COLOR_PRIMARY);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel inputGrid = new JPanel(new GridLayout(2, 3, 14, 10));
        inputGrid.setOpaque(false);

        // Column 1: Member selector or fixed label
        inputGrid.add(new JLabel("Contributing Member:"));
        memberSelectPanel = new JPanel(new CardLayout());
        memberSelectPanel.setOpaque(false);

        cmbMembers = new JComboBox<String>();
        lblFixedMember = new JLabel();
        lblFixedMember.setFont(UITheme.FONT_BOLD);
        lblFixedMember.setForeground(UITheme.COLOR_PRIMARY);

        memberSelectPanel.add(cmbMembers, "DROPDOWN");
        memberSelectPanel.add(lblFixedMember, "FIXED");
        inputGrid.add(memberSelectPanel);

        // Column 2: Hours
        inputGrid.add(new JLabel("Hours Spent (Numeric > 0):"));
        txtHours = new JTextField();
        inputGrid.add(txtHours);

        // Column 3: Description
        inputGrid.add(new JLabel("Contribution Description / Work Details:"));
        txtDescription = new JTextField();
        inputGrid.add(txtDescription);

        formCard.add(inputGrid, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBar.setOpaque(false);
        btnSubmit = UITheme.createPrimaryButton("Submit Contribution");
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmitContribution();
            }
        });
        btnBar.add(btnSubmit);
        formCard.add(btnBar, BorderLayout.SOUTH);

        centerPanel.add(formCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tblTitle = new JLabel("Contribution History Log");
        tblTitle.setFont(UITheme.FONT_HEADER);
        tableCard.add(tblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Member ID", "Member Name", "Hours", "Description", "Logged Timestamp"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        contribTable = new JTable(tableModel);
        UITheme.styleTable(contribTable);

        JScrollPane scrollPane = new JScrollPane(contribTable);
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

        CardLayout cl = (CardLayout) memberSelectPanel.getLayout();
        cmbMembers.removeAllItems();

        if (currentUser.canManageTeam()) {
            cl.show(memberSelectPanel, "DROPDOWN");
            Repository<Member> members = service.getMemberRepository();
            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                cmbMembers.addItem(m.getId() + " - " + m.getName() + " (" + m.getRole() + ")");
            }
        } else {
            cl.show(memberSelectPanel, "FIXED");
            lblFixedMember.setText(currentUser.getName() + " (" + currentUser.getId() + ")");
        }

        // Populate Table
        tableModel.setRowCount(0);
        Repository<Contribution> logs = service.getContributionsForActor(currentUser);
        for (int i = 0; i < logs.size(); i++) {
            Contribution c = logs.get(i);
            tableModel.addRow(new Object[]{
                    c.getMemberId(),
                    c.getMemberName(),
                    String.format("%.1f hrs", c.getHours()),
                    c.getDescription(),
                    c.getDateTimestamp()
            });
        }
    }

    private void handleSubmitContribution() {
        if (currentUser == null) return;

        String targetMemberId;
        if (currentUser.canManageTeam()) {
            String selected = (String) cmbMembers.getSelectedItem();
            if (selected == null || selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a member to log contributions for.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // extract member ID before dash
            targetMemberId = selected.split(" - ")[0].trim();
        } else {
            targetMemberId = currentUser.getId(); // Enforced automatically for students
        }

        String hoursStr = txtHours.getText();
        String description = txtDescription.getText();

        if (hoursStr == null || hoursStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contribution hours cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double hours;
        try {
            hours = Double.parseDouble(hoursStr.trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format for hours: '" + hoursStr + "'. Please enter a valid decimal number.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Contribution logged = service.recordContribution(currentUser, targetMemberId, hours, description);
            JOptionPane.showMessageDialog(this,
                    "Successfully logged " + String.format("%.1f", logged.getHours()) + " hours for " + logged.getMemberName() + "!",
                    "Contribution Recorded", JOptionPane.INFORMATION_MESSAGE);

            txtHours.setText("");
            txtDescription.setText("");

            callback.refreshAllViews();
        } catch (InvalidContributionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Contribution", JOptionPane.WARNING_MESSAGE);
        } catch (MemberNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Member Not Found", JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
