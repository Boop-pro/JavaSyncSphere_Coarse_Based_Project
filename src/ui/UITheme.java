package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import model.Task;

/**
 * Centralized Design System & Theme configuration for SyncSphere.
 * Provides custom colors, fonts, rounded cards, buttons, and custom table renderers.
 */
public class UITheme {

    // Color Palette
    public static final Color COLOR_PRIMARY = new Color(67, 56, 202);      // Indigo 700
    public static final Color COLOR_PRIMARY_HOVER = new Color(79, 70, 229); // Indigo 600
    public static final Color COLOR_ACCENT = new Color(37, 99, 235);        // Blue 600
    public static final Color COLOR_BG = new Color(248, 250, 252);          // Slate 50
    public static final Color COLOR_SIDEBAR = new Color(15, 23, 42);        // Slate 900
    public static final Color COLOR_SIDEBAR_TEXT = new Color(226, 232, 240);// Slate 200
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(30, 41, 59); // Slate 800
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(226, 232, 240);      // Slate 200
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // Slate 900
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // Slate 500

    // Status Colors
    public static final Color COLOR_PENDING_BG = new Color(254, 243, 199);  // Amber 100
    public static final Color COLOR_PENDING_TXT = new Color(180, 83, 9);    // Amber 700
    public static final Color COLOR_COMPL_BG = new Color(219, 234, 254);    // Blue 100
    public static final Color COLOR_COMPL_TXT = new Color(29, 78, 216);     // Blue 700
    public static final Color COLOR_APPRV_BG = new Color(209, 250, 229);    // Emerald 100
    public static final Color COLOR_APPRV_TXT = new Color(4, 120, 87);      // Emerald 700

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT_NUM = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Creates a styled card JPanel with subtle border and white background.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    /**
     * Creates a stat card displaying a number, label, and accent line.
     */
    public static JPanel createStatCard(String label, String initialValue, Color accentColor) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 8));

        JLabel numLabel = new JLabel(initialValue);
        numLabel.setFont(FONT_STAT_NUM);
        numLabel.setForeground(accentColor);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(FONT_STAT_LABEL);
        textLabel.setForeground(COLOR_TEXT_MUTED);

        JPanel topBar = new JPanel();
        topBar.setBackground(accentColor);
        topBar.setPreferredSize(new Dimension(100, 3));

        card.add(topBar, BorderLayout.NORTH);
        card.add(numLabel, BorderLayout.CENTER);
        card.add(textLabel, BorderLayout.SOUTH);

        card.putClientProperty("numLabel", numLabel);
        return card;
    }

    /**
     * Creates a styled primary action button.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    /**
     * Creates a styled secondary button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        return btn;
    }

    /**
     * Styles standard JTable with modern font, padding, and headers.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(238, 242, 255)); // Light indigo
        table.setSelectionForeground(COLOR_PRIMARY);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(COLOR_BORDER);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(new Color(241, 245, 249)); // Slate 100
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);
    }

    /**
     * Custom TableCellRenderer for Task Status badges.
     */
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(FONT_BADGE);
            label.setOpaque(true);

            String status = (value != null) ? value.toString() : "";
            if (Task.STATUS_PENDING.equalsIgnoreCase(status)) {
                label.setBackground(COLOR_PENDING_BG);
                label.setForeground(COLOR_PENDING_TXT);
                label.setText("  PENDING  ");
            } else if (Task.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                label.setBackground(COLOR_COMPL_BG);
                label.setForeground(COLOR_COMPL_TXT);
                label.setText("  COMPLETED  ");
            } else if (Task.STATUS_APPROVED.equalsIgnoreCase(status)) {
                label.setBackground(COLOR_APPRV_BG);
                label.setForeground(COLOR_APPRV_TXT);
                label.setText("  APPROVED  ");
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }

            label.setBorder(new EmptyBorder(4, 8, 4, 8));
            return label;
        }
    }
}
