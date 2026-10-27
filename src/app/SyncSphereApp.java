package app;

import java.awt.*;
import javax.swing.*;
import model.Member;
import service.ContributionService;
import ui.LoginPanel;
import ui.MainDashboardShell;
import ui.NavigationCallback;

/**
 * Main application entry point for SyncSphere.
 * Demonstrates:
 * - FlatLaf Light theme initialization with graceful fallback to standard Swing L&F
 * - SwingUtilities.invokeLater() for Event Dispatch Thread (EDT) safety
 * - JFrame configuration (1200x750, resizable, minimum bounds)
 * - Navigation orchestration between Login/First-Run and Main Application Shell
 */
public class SyncSphereApp implements NavigationCallback {

    private JFrame mainFrame;
    private JPanel rootPanel;
    private CardLayout rootCardLayout;

    private ContributionService service;
    private Member currentUser;

    private LoginPanel loginPanel;
    private MainDashboardShell mainShell;

    public static final String VIEW_LOGIN = "LOGIN";
    public static final String VIEW_MAIN = "MAIN";

    public SyncSphereApp() {
        this.service = new ContributionService();
    }

    public void initAndShowGUI() {
        // Initialize Look and Feel (FlatLaf with fallback)
        setupLookAndFeel();

        mainFrame = new JFrame("SyncSphere — Team Contribution & Task Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 750);
        mainFrame.setMinimumSize(new Dimension(1000, 650));
        mainFrame.setLocationRelativeTo(null);

        // Root container with CardLayout
        rootCardLayout = new CardLayout();
        rootPanel = new JPanel(rootCardLayout);

        // Child views
        loginPanel = new LoginPanel(service, this);
        mainShell = new MainDashboardShell(service, this);

        rootPanel.add(loginPanel, VIEW_LOGIN);
        rootPanel.add(mainShell, VIEW_MAIN);

        mainFrame.getContentPane().add(rootPanel);

        // Check if accounts exist; show appropriate initial screen
        loginPanel.refreshState();
        rootCardLayout.show(rootPanel, VIEW_LOGIN);

        mainFrame.setVisible(true);
    }

    /**
     * Attempts to configure FlatLaf Light Look & Feel.
     * Falls back to System Look & Feel or standard Swing gracefully if FlatLaf is unavailable.
     */
    private static void setupLookAndFeel() {
        try {
            // Attempt FlatLaf via reflection or direct call so code works even if flatlaf is absent
            Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            java.lang.reflect.Method setupMethod = flatLafClass.getMethod("setup");
            setupMethod.invoke(null);
            System.out.println("[Look & Feel] Successfully initialized FlatLaf Light theme.");
        } catch (Throwable t) {
            System.err.println("[Look & Feel] FlatLaf could not be initialized (" + t.getMessage() + "). Falling back to System Look and Feel.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[Look & Feel] System Look and Feel fallback failed. Using default Swing Look and Feel.");
            }
        }
    }

    // ==========================================
    // NAVIGATION CALLBACK IMPLEMENTATION
    // ==========================================

    @Override
    public void onLoginSuccess(Member loggedInUser) {
        this.currentUser = loggedInUser;
        mainShell.setUser(currentUser);
        rootCardLayout.show(rootPanel, VIEW_MAIN);
    }

    @Override
    public void onLogout() {
        this.currentUser = null;
        loginPanel.refreshState();
        rootCardLayout.show(rootPanel, VIEW_LOGIN);
    }

    @Override
    public void showView(String viewName) {
        mainShell.switchView(viewName);
    }

    @Override
    public void refreshAllViews() {
        mainShell.refreshAll();
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SyncSphereApp app = new SyncSphereApp();
                app.initAndShowGUI();
            }
        });
    }
}
