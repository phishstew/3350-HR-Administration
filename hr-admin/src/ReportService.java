// package com.companyz.ems.reports;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.sql.DataSource;

public class ReportService {
    private final DataSource dataSource;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Generates pay statement history for employees.
     * - For admin users: Returns data for all employees
     * - For regular users: Returns only the authenticated user's data
     *
     * @param isAdmin whether the requesting user is an administrator
     * @param employeeId the ID of the requesting employee (used for permission check)
     * @return a list of PayStatementRecord objects
     * @throws ReportException if database errors occur
     */
    public List<PayStatementRecord> generatePayStatementHistory(boolean isAdmin, int employeeId) throws ReportException {
        List<PayStatementRecord> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT p.payroll_id, e.empid, e.first_name, e.last_name, " +
                    "p.pay_date, p.earnings, p.fed_tax, p.fed_med, p.fed_ss, " +
                    "p.state_tax, p.retire_401k, p.healthcare " +
                    "FROM payroll p JOIN employees e ON p.empid = e.empid ";
                    
            // If not admin, add a WHERE clause to restrict to current employee only
            if (!isAdmin) {
                sql += "WHERE e.empid = ? ";
            }
            
            // Add the ORDER BY clause for sorting as per requirements
            sql += "ORDER BY e.empid, p.pay_date";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Set parameter for non-admin users
                if (!isAdmin) {
                    stmt.setInt(1, employeeId);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        PayStatementRecord record = new PayStatementRecord(
                            rs.getInt("payroll_id"),
                            rs.getInt("empid"),
                            rs.getString("first_name") + " " + rs.getString("last_name"),
                            rs.getDate("pay_date").toLocalDate(),
                            rs.getBigDecimal("earnings"),
                            rs.getBigDecimal("fed_tax"),
                            rs.getBigDecimal("fed_med"),
                            rs.getBigDecimal("fed_ss"),
                            rs.getBigDecimal("state_tax"),
                            rs.getBigDecimal("retire_401k"),
                            rs.getBigDecimal("healthcare")
                        );
                        results.add(record);
                    }
                }
            }
            
            return results;
            
        } catch (SQLException e) {
            throw new ReportException("Failed to generate pay statement history report", e);
        }
    }

    /**
     * Generates a report showing total pay by job title for a specific month.
     * Admin only functionality.
     *
     * @param yearMonth the month to report on (format: YYYY-MM)
     * @param isAdmin whether the requesting user is an administrator
     * @return a map of job titles to total pay amounts
     * @throws ReportException if database errors occur or unauthorized access attempt
     */
    public Map<String, BigDecimal> generateTotalPayByJobTitle(YearMonth yearMonth, boolean isAdmin) throws ReportException {
        if (!isAdmin) {
            throw new ReportException("Permission denied: Admin access required for job title reports");
        }
        
        Map<String, BigDecimal> results = new LinkedHashMap<>(); // Maintains insertion order for consistent display
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT jt.job_title as title, SUM(p.earnings) as total_pay " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.empid = e.empid " +
                    "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                    "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                    "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? " +
                    "GROUP BY jt.job_title " +
                    "ORDER BY total_pay DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, yearMonth.getYear());
                stmt.setInt(2, yearMonth.getMonthValue());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String jobTitle = rs.getString("title");
                        BigDecimal totalPay = rs.getBigDecimal("total_pay");
                        results.put(jobTitle, totalPay);
                    }
                }
            }
            
            return results;
            
        } catch (SQLException e) {
            throw new ReportException("Failed to generate total pay by job title report", e);
        }
    }

    /**
     * Generates a report showing total pay by division for a specific month.
     * Admin only functionality.
     *
     * @param yearMonth the month to report on (format: YYYY-MM)
     * @param isAdmin whether the requesting user is an administrator
     * @return a map of divisions to total pay amounts
     * @throws ReportException if database errors occur or unauthorized access attempt
     */
    public Map<String, BigDecimal> generateTotalPayByDivision(YearMonth yearMonth, boolean isAdmin) throws ReportException {
        if (!isAdmin) {
            throw new ReportException("Permission denied: Admin access required for division reports");
        }
        
        Map<String, BigDecimal> results = new LinkedHashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT d.div_name as division, SUM(p.earnings) as total_pay " +
                    "FROM payroll p " +
                    "JOIN employees e ON p.empid = e.empid " +
                    "JOIN employee_division ed ON e.empid = ed.empid " +
                    "JOIN division d ON ed.div_id = d.div_id " +
                    "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? " +
                    "GROUP BY d.div_name " +
                    "ORDER BY total_pay DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, yearMonth.getYear());
                stmt.setInt(2, yearMonth.getMonthValue());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String division = rs.getString("division");
                        BigDecimal totalPay = rs.getBigDecimal("total_pay");
                        results.put(division, totalPay);
                    }
                }
            }
            
            return results;
            
        } catch (SQLException e) {
            throw new ReportException("Failed to generate total pay by division report", e);
        }
    }
    
    /**
     * Exports a pay statement report to PDF format.
     * 
     * @param report the report data to export
     * @param outputPath the file path to save the PDF
     * @throws ReportException if PDF generation fails
    //  */
    // public void exportPayStatementToPdf(List<PayStatementRecord> report, String outputPath) throws ReportException {
    //     // Implementation would use a PDF library like iText or Apache PDFBox
    //     // This is a placeholder for the actual implementation
    //     System.out.println("Exporting pay statement report to PDF: " + outputPath);
    //     // In a real implementation, you would:
    //     // 1. Create a new PDF document
    //     // 2. Add report title and headers
    //     // 3. Loop through the report data and add to PDF table
    //     // 4. Add summary information
    //     // 5. Save the PDF to the specified path
    // }
}