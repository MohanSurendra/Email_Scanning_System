package com.example.web;

import javax.swing.*;
import java.awt.*;

public class LogForm extends JPanel {
	protected JTextArea logsArea;

    public LogForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane logsScrollPane = new JScrollPane(logsArea);
        add(logsScrollPane, BorderLayout.CENTER);
    }

    public JTextArea getLogsArea() {
        return logsArea;
    }
}
