package com.example.web;

import com.example.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ManageRulesForm extends JDialog {
    protected JTextArea rulesTextArea;
    protected List<String> rules;

    public ManageRulesForm(List<String> rules) {
        this.rules = rules;
        setTitle("Manage Rules");
        setSize(400, 300);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        rulesTextArea = new JTextArea();
        rulesTextArea.setLineWrap(true);
        rulesTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(rulesTextArea);

        for (String rule : rules) {
            rulesTextArea.append(rule + "\n");
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRules();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRules();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void saveRules() {
        String[] newRules = rulesTextArea.getText().split("\\n");
        try {
            // Clear existing rules
            for (String rule : rules) {
                DatabaseConnection.deleteRule(rule);
            }
            rules.clear();

            // Save new rules
            for (String rule : newRules) {
                if (!rule.trim().isEmpty()) {
                    DatabaseConnection.saveRule(rule.trim());
                    rules.add(rule.trim());
                }
            }
            JOptionPane.showMessageDialog(this, "Rules saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving rules.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRules() {
        try {
            // Clear all rules
            for (String rule : rules) {
                DatabaseConnection.deleteRule(rule);
            }
            rules.clear();
            rulesTextArea.setText("");
            JOptionPane.showMessageDialog(this, "All rules deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting rules.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
