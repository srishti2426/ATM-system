package com.atm;

import com.atm.ui.ATMFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ATMFrame().setVisible(true);
        });
    }
} 