package ui;

import model.Member;

/**
 * Callback contract for UI navigation and state changes.
 * Demonstrates loose coupling and Event-driven UI architecture.
 */
public interface NavigationCallback {
    void onLoginSuccess(Member loggedInUser);
    void onLogout();
    void showView(String viewName);
    void refreshAllViews();
}
