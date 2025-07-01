package com.example.web;

import javax.swing.*;
import java.awt.*;

public class ViewLogsForm extends JDialog {
	protected JTable logsTable;

    public ViewLogsForm(JFrame parent) {
        super(parent, "View Logs", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Date", "Email", "Suspicion Level"};
        Object[][] data = {
            {"2024-06-15", "example1@example.com", "High"},
            {"2024-06-14", "example2@example.com", "Low"}
        };

        logsTable = new JTable(data, columnNames);
        logsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        logsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(logsTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
    }
}
