package ui;

import exception.DuplicateMemberException;
import exception.MemberNotFoundException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.Member;
import service.ContributionService;

/**
 * Login and First-Run Account Creation Panel.
 * Implements Section 7 (First-Run Account Creation) and Section 8 (User Login).
 */
public class LoginPanel extends JPanel {

    private ContributionService service;
    private NavigationCallback callback;

    private JPanel cardContainer;
    private CardLayout cardLayout;

    // First-run fields
    private JTextField txtFirstId;
    private JTextField txtFirstName;
    private JTextField txtFirstEmail;
    private JComboBox<String> cmbFirstRole;

    // Login fields
    private JTextField txtLoginId;

    public LoginPanel(ContributionService service, NavigationCallback callback) {
        this.service = service;
        this.callback = callback;

        setLayout(new GridBagLayout());
        setBackground(UITheme.COLOR_BG);

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        // Build both cards
        JPanel firstRunPanel = buildFirstRunCard();
        JPanel loginCard = buildLoginCard();

        cardContainer.add(firstRunPanel, "FIRST_RUN");
        cardContainer.add(loginCard, "LOGIN");

        add(cardContainer);
        refreshState();
    }

    public void refreshState() {
        if (service.getMemberRepository().isEmpty()) {
            cardLayout.show(cardContainer, "FIRST_RUN");
        } else {
            cardLayout.show(cardContainer, "LOGIN");
            txtLoginId.setText("");
            txtLoginId.requestFocusInWindow();
        }
    }

    /**
     * Card for First-Run Account Creation (Section 7).
     */
    private JPanel buildFirstRunCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 480));
        card.setMaximumSize(new Dimension(460, 480));

        JLabel title = new JLabel("Welcome to SyncSphere");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.COLOR_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Create Your First Account (System Initialization)");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.COLOR_TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 14));
        form.setOpaque(false);

        form.add(new JLabel("Member ID / Roll No:"));
        txtFirstId = new JTextField();
        form.add(txtFirstId);

        form.add(new JLabel("Full Name:"));
        txtFirstName = new JTextField();
        form.add(txtFirstName);

        form.add(new JLabel("Email Address:"));
        txtFirstEmail = new JTextField();
        form.add(txtFirstEmail);

        form.add(new JLabel("Account Role:"));
        cmbFirstRole = new JComboBox<String>(new String[]{"Team Lead", "Student Member"});
        form.add(cmbFirstRole);

        card.add(form);
        card.add(Box.createVerticalStrut(24));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnCreate = UITheme.createPrimaryButton("Create Account & Login");
        JButton btnExit = UITheme.createSecondaryButton("Exit");

        btnPanel.add(btnCreate);
        btnPanel.add(btnExit);
        card.add(btnPanel);

        // Action Listeners
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFirstRunCreate();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return card;
    }

    /**
     * Card for Member ID Login (Section 8).
     */
    private JPanel buildLoginCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 420));
        card.setMaximumSize(new Dimension(460, 420));

        JLabel logo = new JLabel("SYNCSHPERE");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(UITheme.COLOR_PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("Team Contribution & Task Management");
        desc.setFont(UITheme.FONT_SUBTITLE);
        desc.setForeground(UITheme.COLOR_TEXT_MUTED);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(desc);
        card.add(Box.createVerticalStrut(32));

        JLabel lblId = new JLabel("Member ID / Roll Number:");
        lblId.setFont(UITheme.FONT_BOLD);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblId);
        card.add(Box.createVerticalStrut(8));

        txtLoginId = new JTextField();
        txtLoginId.setPreferredSize(new Dimension(280, 38));
        txtLoginId.setMaximumSize(new Dimension(280, 38));
        txtLoginId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtLoginId.setHorizontalAlignment(JTextField.CENTER);
        card.add(txtLoginId);
        card.add(Box.createVerticalStrut(24));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnLogin = UITheme.createPrimaryButton("Login");
        JButton btnExit = UITheme.createSecondaryButton("Exit");

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        card.add(btnPanel);

        // Key action on enter in login textfield
        txtLoginId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return card;
    }

    private void handleFirstRunCreate() {
        String id = txtFirstId.getText();
        String name = txtFirstName.getText();
        String email = txtFirstEmail.getText();
        String role = (String) cmbFirstRole.getSelectedItem();

        try {
            Member member = service.registerMember(null, id, name, email, role);
            JOptionPane.showMessageDialog(this,
                    "Welcome, " + member.getName() + "!\nYour account has been created successfully as " + member.getRole() + ".",
                    "Account Created", JOptionPane.INFORMATION_MESSAGE);
            callback.onLoginSuccess(member);
        } catch (DuplicateMemberException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate ID", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogin() {
        String id = txtLoginId.getText();
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Member ID / Roll Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Member member = service.findMemberById(id.trim());
            callback.onLoginSuccess(member);
        } catch (MemberNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Member ID '" + id.trim() + "' does not exist in the system.\nPlease contact your Team Lead for registration.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
