package com.example.web;

import com.example.database.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportsForm {
	protected JPanel mainPanel;

    public ReportsForm() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Email Reports"));

        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 16));
        generateReportButton.setBackground(new Color(70, 130, 180));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setFocusPainted(false);
        generateReportButton.addActionListener(e -> generateReport());

        mainPanel.add(generateReportButton, BorderLayout.NORTH);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    private void generateReport() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String query = "SELECT suspicious_percentage, COUNT(*) AS count FROM email_scans GROUP BY suspicious_percentage";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                double percentage = resultSet.getDouble("suspicious_percentage");
                int count = resultSet.getInt("count");
                dataset.addValue(count, "Emails", String.valueOf(percentage));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Suspicious Email Reports",
                "Suspicious Percentage",
                "Number of Emails",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(560, 367));
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
    }
}
