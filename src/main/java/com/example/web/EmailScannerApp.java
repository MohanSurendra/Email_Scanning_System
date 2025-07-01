package com.example.web;

import com.example.database.DatabaseConnection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmailScannerApp extends JFrame {
	protected JTextArea emailContentArea;
	protected JTextArea logsArea;
	protected List<String> rules;

    public EmailScannerApp() {
        setTitle("Email Scanner Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rules = new ArrayList<>();
        initUI();
        DatabaseConnection.initializeDatabase();
        loadLogs();
        loadRules();
    }

    protected void initUI() {
        // Set Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout());

        emailContentArea = new JTextArea();
        emailContentArea.setLineWrap(true);
        emailContentArea.setWrapStyleWord(true);
        emailContentArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        JScrollPane emailScrollPane = new JScrollPane(emailContentArea);
        emailScrollPane.setPreferredSize(new Dimension(getWidth() * 40 / 100, getHeight() * 50 / 100));
        emailScrollPane.setBorder(BorderFactory.createTitledBorder("Email Content"));

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton scanButton = new JButton("Scan");
        scanButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        scanButton.setBackground(new java.awt.Color(0, 122, 204));
        scanButton.setForeground(java.awt.Color.WHITE);
        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startScan();
            }
        });

        inputPanel.add(new JLabel());  // Spacer
        inputPanel.add(scanButton);

        logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        JScrollPane logScrollPane = new JScrollPane(logsArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Logs"));

        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(logScrollPane, BorderLayout.CENTER);

        JPanel navbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton manageRulesButton = new JButton("Manage Rules");
        JButton viewLogsButton = new JButton("View Logs");
        JButton generateReportButton = new JButton("Generate Report");
        JButton exportLogsButton = new JButton("Export Logs");

        manageRulesButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        viewLogsButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        generateReportButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        exportLogsButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        manageRulesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageRules();
            }
        });

        viewLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewLogs();
            }
        });

        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        exportLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportLogs();
            }
        });

        navbar.add(manageRulesButton);
        navbar.add(viewLogsButton);
        navbar.add(generateReportButton);
        navbar.add(exportLogsButton);

        panel.add(navbar, BorderLayout.NORTH);
        panel.add(emailScrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    protected void startScan() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String emailContent = emailContentArea.getText();
                double suspiciousPercentage = calculateSuspiciousPercentage(emailContent);

                String logEntry = "Scanned email with suspicious percentage: " + suspiciousPercentage + "%\n";
                logsArea.append(logEntry);
                saveLog(logEntry);
            }
        });
    }

    protected double calculateSuspiciousPercentage(String content) {
        if (content.isEmpty() || rules.isEmpty()) {
            return 0.0;
        }

        int totalKeywords = rules.size();
        int foundKeywords = 0;

        for (String keyword : rules) {
            if (content.contains(keyword.trim())) {
                foundKeywords++;
            }
        }

        return (foundKeywords / (double) totalKeywords) * 100;
    }

    protected void saveLog(String logEntry) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO logs (log_entry) VALUES (?)")) {
            statement.setString(1, logEntry);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void loadLogs() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT log_entry FROM logs")) {
            while (resultSet.next()) {
                logsArea.append(resultSet.getString("log_entry"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void loadRules() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT rule FROM rules")) {
            rules.clear();
            while (resultSet.next()) {
                rules.add(resultSet.getString("rule"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void manageRules() {
        JDialog manageRulesDialog = new JDialog(this, "Manage Rules", true);
        manageRulesDialog.setSize(400, 300);
        manageRulesDialog.setLayout(new BorderLayout());

        JTextArea rulesArea = new JTextArea();
        rulesArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        rulesArea.setText(String.join("\n", rules));
        JScrollPane rulesScrollPane = new JScrollPane(rulesArea);
        manageRulesDialog.add(rulesScrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        saveButton.setBackground(new java.awt.Color(0, 122, 204));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] newRules = rulesArea.getText().split("\n");
                rules.clear();
                for (String rule : newRules) {
                    if (!rule.trim().isEmpty()) {
                        rules.add(rule.trim());
                    }
                }
                saveRules();
                manageRulesDialog.dispose();
            }
        });
        manageRulesDialog.add(saveButton, BorderLayout.SOUTH);

        manageRulesDialog.setLocationRelativeTo(this);
        manageRulesDialog.setVisible(true);
    }

    protected void saveRules() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM rules");
            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO rules (rule) VALUES (?)")) {
                for (String rule : rules) {
                    pstmt.setString(1, rule);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void viewLogs() {
        JDialog viewLogsDialog = new JDialog(this, "View Logs", true);
        viewLogsDialog.setSize(600, 400);
        viewLogsDialog.setLayout(new BorderLayout());

        JTextArea viewLogsArea = new JTextArea(logsArea.getText());
        viewLogsArea.setEditable(false);
        viewLogsArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        JScrollPane viewLogsScrollPane = new JScrollPane(viewLogsArea);
        viewLogsDialog.add(viewLogsScrollPane, BorderLayout.CENTER);

        viewLogsDialog.setLocationRelativeTo(this);
        viewLogsDialog.setVisible(true);
    }

    protected void generateReport() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT log_entry FROM logs")) {

            int emailNumber = 1;
            while (resultSet.next()) {
                String logEntry = resultSet.getString("log_entry");
                double percentage = extractPercentage(logEntry);
                dataset.addValue(percentage, "Suspicious Percentage", "Email " + emailNumber);
                emailNumber++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Email Suspicious Percentage Report",
                "Emails",
                "Percentage",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JFrame reportFrame = new JFrame("Suspicious Percentage Report");
        reportFrame.setSize(800, 600);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportFrame.add(chartPanel);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setVisible(true);
    }

    protected double extractPercentage(String logEntry) {
        String[] parts = logEntry.split(":");
        if (parts.length > 1) {
            try {
                return Double.parseDouble(parts[1].replace("%", "").trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    protected void exportLogs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Logs As");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF, Excel, and CSV files", "pdf", "xlsx", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (filePath.endsWith(".pdf")) {
                exportLogsToPDF(fileToSave);
            } else if (filePath.endsWith(".xlsx")) {
                exportLogsToExcel(fileToSave);
            } else if (filePath.endsWith(".csv")) {
                exportLogsToCSV(fileToSave);
            } else {
                JOptionPane.showMessageDialog(this, "Unsupported file format. Please choose PDF, Excel, or CSV.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void exportLogsToPDF(File file) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph("Email Scanner Logs", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));
            document.add(new Paragraph(logsArea.getText(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.close();
            JOptionPane.showMessageDialog(this, "Logs exported to PDF successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting logs to PDF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void exportLogsToExcel(File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Logs");
            String[] logEntries = logsArea.getText().split("\n");

            for (int i = 0; i < logEntries.length; i++) {
                Row row = sheet.createRow(i);
                Cell cell = row.createCell(0);
                cell.setCellValue(logEntries[i]);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this, "Logs exported to Excel successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting logs to Excel.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void exportLogsToCSV(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Log Entry\n");
            writer.write(logsArea.getText().replace("\n", "\r\n"));
            JOptionPane.showMessageDialog(this, "Logs exported to CSV successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting logs to CSV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmailScannerApp().setVisible(true);
            }
        });
    }
}
