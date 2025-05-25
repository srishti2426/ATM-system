package com.atm.util;

import javax.swing.*;
import java.awt.*;

public class UIUtil {
    // Colors
    public static final Color PRIMARY_COLOR = new Color(0, 122, 204);
    public static final Color SECONDARY_COLOR = new Color(45, 45, 45);
    public static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final Color TEXT_COLOR = new Color(33, 33, 33);
    
    // Fonts
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    public static JLabel createHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(REGULAR_FONT);
        textField.setPreferredSize(new Dimension(200, 30));
        return textField;
    }
    
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(REGULAR_FONT);
        passwordField.setPreferredSize(new Dimension(200, 30));
        return passwordField;
    }
    
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
} 