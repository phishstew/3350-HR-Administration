package com.companyz.ems.reports;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.sql.DataSource;

/**
 * Service responsible for generating various reports in the Employee Management System.
 * Implements permission controls based on user roles.
 */


/**
 * Data Transfer Object (DTO) for pay statement records.
 */


/**
 * Custom exception for report-related errors.
 */


/**
 * JavaFX Controller for the Pay Statement Report View.
 */
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PayStatementReportController {
    @FXML private TableView<PayStatementRecord> tableView;
    @FXML private DatePicker monthPicker;
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private Button generateButton;
    @FXML private Button exportButton;
    @FXML private Label statusLabel;
    @FXML private Label titleLabel;
    
    private final ReportService reportService;
    private final boolean isAdmin;
    private final int currentEmployeeId;
    
    public PayStatementReportController(ReportService reportService, boolean isAdmin, int currentEmployeeId) {
        this.reportService = reportService;
        this.isAdmin = isAdmin;
        this.currentEmployeeId = currentEmployeeId;
    }
    
    @FXML
    public void initialize() {
        // Set up table columns
        setupTableColumns();
        
        // Set up report type combo box
        reportTypeComboBox.getItems().add("Pay Statement History");
        
        // Add admin-only report types
        if (isAdmin) {
            reportTypeComboBox.getItems().add("Total Pay by Job Title");
            reportTypeComboBox.getItems().add("Total Pay by Division");
        }
        
        reportTypeComboBox.getSelectionModel().selectFirst();
        
        // Set up month picker with formatter
        // Note: In practice, you might need a custom DateCell factory to restrict to month view
        
        // Initially disable export button until report is generated
        exportButton.setDisable(true);
    }
    
    private void setupTableColumns() {
        // Setup will differ based on report type
        // Initially set up for pay statement history
        tableView.getColumns().clear();
        
        TableColumn<PayStatementRecord, String> nameCol = new TableColumn<>("Employee");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        
        TableColumn<PayStatementRecord, String> dateCol = new TableColumn<>("Pay Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedPayDate"));
        
        TableColumn<PayStatementRecord, BigDecimal> earningsCol = new TableColumn<>("Gross Pay");
        earningsCol.setCellValueFactory(new PropertyValueFactory<>("earnings"));
        
        TableColumn<PayStatementRecord, BigDecimal> netPayCol = new TableColumn<>("Net Pay");
        netPayCol.setCellValueFactory(new PropertyValueFactory<>("netPay"));
        
        tableView.getColumns().addAll(nameCol, dateCol, earningsCol, netPayCol);
    }
    
    @FXML
    private void handleGenerateReport(ActionEvent event) {
        exportButton.setDisable(true);
        String reportType = reportTypeComboBox.getValue();
        
        try {
            if ("Pay Statement History".equals(reportType)) {
                generatePayStatementHistory();
            } else if ("Total Pay by Job Title".equals(reportType)) {
                generateTotalPayByJobTitle();
            } else if ("Total Pay by Division".equals(reportType)) {
                generateTotalPayByDivision();
            }
            
            exportButton.setDisable(false);
            statusLabel.setText("Report generated successfully");
            
        } catch (ReportException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
    
    private void generatePayStatementHistory() throws ReportException {
        titleLabel.setText("Pay Statement History Report");
        setupTableColumns(); // Reset to default columns
        
        List<PayStatementRecord> records = reportService.generatePayStatementHistory(isAdmin, currentEmployeeId);
        tableView.setItems(FXCollections.observableArrayList(records));
    }
    
    private void generateTotalPayByJobTitle() throws ReportException {
        if (!isAdmin) {
            throw new ReportException("Admin access required");
        }
        
        YearMonth selectedMonth = YearMonth.from(monthPicker.getValue());
        titleLabel.setText("Total Pay by Job Title - " + selectedMonth.format(MONTH_FORMATTER));
        
        // Reconfigure table for different data structure
        tableView.getColumns().clear();
        
        TableColumn<Map.Entry<String, BigDecimal>, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        
        TableColumn<Map.Entry<String, BigDecimal>, BigDecimal> totalCol = new TableColumn<>("Total Pay");
        totalCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
        
        tableView.getColumns().addAll(titleCol, totalCol);
        
        // Get report data
        Map<String, BigDecimal> reportData = reportService.generateTotalPayByJobTitle(selectedMonth, true);
        
        // Convert to observable list of entries for TableView
        tableView.setItems(FXCollections.observableArrayList(reportData.entrySet()));
    }
    
    private void generateTotalPayByDivision() throws ReportException {
        if (!isAdmin) {
            throw new ReportException("Admin access required");
        }
        
        YearMonth selectedMonth = YearMonth.from(monthPicker.getValue());
        titleLabel.setText("Total Pay by Division - " + selectedMonth.format(MONTH_FORMATTER));
        
        // Reconfigure table similar to job title report
        tableView.getColumns().clear();
        
        TableColumn<Map.Entry<String, BigDecimal>, String> divisionCol = new TableColumn<>("Division");
        divisionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        
        TableColumn<Map.Entry<String, BigDecimal>, BigDecimal> totalCol = new TableColumn<>("Total Pay");
        totalCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
        
        tableView.getColumns().addAll(divisionCol, totalCol);
        
        // Get report data
        Map<String, BigDecimal> reportData = reportService.generateTotalPayByDivision(selectedMonth, true);
        
        // Convert to observable list of entries for TableView
        tableView.setItems(FXCollections.observableArrayList(reportData.entrySet()));
    }
    
    @FXML
    private void handleExportToPdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        if (file != null) {
            try {
                // The export implementation would differ based on report type
                // For simplicity, we're just showing the pay statement export
                if ("Pay Statement History".equals(reportTypeComboBox.getValue())) {
                    @SuppressWarnings("unchecked")
                    List<PayStatementRecord> data = (List<PayStatementRecord>) tableView.getItems();
                    reportService.exportPayStatementToPdf(data, file.getAbsolutePath());
                    statusLabel.setText("Report exported to " + file.getName());
                } else {
                    // Handle other report types...
                    statusLabel.setText("Export not implemented for this report type");
                }
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }
}