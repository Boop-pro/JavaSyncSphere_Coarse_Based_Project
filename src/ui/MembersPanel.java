package ui;

import exception.DuplicateMemberException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Member;
import repository.Repository;
import service.ContributionService;

/**
 * Members Management Panel (Section 12).
 * Allows Team Lead to register new members and view all team members.
 * Enforces role-based access control.
 */
public class MembersPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;
    private Member currentUser;

    private JPanel formPanel;
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JButton btnRegister;
    private JLabel lblAccessBanner;

    private JTable membersTable;
    private DefaultTableModel tableModel;

    public MembersPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Team Member Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_TEXT_MAIN);

        JLabel lblDesc = new JLabel("Register project collaborators and inspect contribution standings");
        lblDesc.setFont(UITheme.FONT_SUBTITLE);
        lblDesc.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblDesc, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center container
        JPanel centerContainer = new JPanel(new BorderLayout(0, 18));
        centerContainer.setOpaque(false);

        // Registration Card
        formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new BorderLayout(0, 14));

        lblAccessBanner = new JLabel("Register New Team Member");
        lblAccessBanner.setFont(UITheme.FONT_HEADER);
        lblAccessBanner.setForeground(UITheme.COLOR_PRIMARY);
        formPanel.add(lblAccessBanner, BorderLayout.NORTH);

        JPanel inputGrid = new JPanel(new GridLayout(2, 4, 12, 10));
        inputGrid.setOpaque(false);

        inputGrid.add(new JLabel("Member ID / Roll No:"));
        txtId = new JTextField();
        inputGrid.add(txtId);

        inputGrid.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        inputGrid.add(txtName);

        inputGrid.add(new JLabel("Email Address:"));
        txtEmail = new JTextField();
        inputGrid.add(txtEmail);

        inputGrid.add(new JLabel("Role:"));
        cmbRole = new JComboBox<String>(new String[]{"Student Member", "Team Lead"});
        inputGrid.add(cmbRole);

        formPanel.add(inputGrid, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBar.setOpaque(false);
        btnRegister = UITheme.createPrimaryButton("Register Member");
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        btnBar.add(btnRegister);
        formPanel.add(btnBar, BorderLayout.SOUTH);

        centerContainer.add(formPanel, BorderLayout.NORTH);

        // Members Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tblTitle = new JLabel("Registered Team Roster");
        tblTitle.setFont(UITheme.FONT_HEADER);
        tableCard.add(tblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Member ID", "Name", "Email", "Role", "Total Hours", "Grade / Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        membersTable = new JTable(tableModel);
        UITheme.styleTable(membersTable);

        JScrollPane scrollPane = new JScrollPane(membersTable);
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

        boolean isLead = currentUser.canManageTeam();
        txtId.setEnabled(isLead);
        txtName.setEnabled(isLead);
        txtEmail.setEnabled(isLead);
        cmbRole.setEnabled(isLead);
        btnRegister.setEnabled(isLead);

        if (!isLead) {
            lblAccessBanner.setText("Register New Team Member (Locked — Only Team Lead can register members)");
            lblAccessBanner.setForeground(UITheme.COLOR_TEXT_MUTED);
        } else {
            lblAccessBanner.setText("Register New Team Member");
            lblAccessBanner.setForeground(UITheme.COLOR_PRIMARY);
        }

        tableModel.setRowCount(0);
        Repository<Member> members = service.getMemberRepository();
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getEmail(),
                    m.getRole(),
                    String.format("%.1f hrs", m.getTotalHours()),
                    m.calculateGrade()
            });
        }
    }

    private void handleRegister() {
        if (currentUser == null || !currentUser.canManageTeam()) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied!\nOnly Team Lead can perform this operation.",
                    "Unauthorized Action", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = txtId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String role = (String) cmbRole.getSelectedItem();

        try {
            Member created = service.registerMember(currentUser, id, name, email, role);
            JOptionPane.showMessageDialog(this,
                    "Member '" + created.getName() + "' (" + created.getId() + ") successfully registered as " + created.getRole() + "!",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            txtId.setText("");
            txtName.setText("");
            txtEmail.setText("");
            cmbRole.setSelectedIndex(0);

            callback.refreshAllViews();
        } catch (DuplicateMemberException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Member Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
