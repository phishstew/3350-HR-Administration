// package com.companyz.ems.reports;

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
public record PayStatementRecord(
    int payrollId,
    int employeeId,
    String employeeName,
    LocalDate payDate,
    BigDecimal earnings,
    BigDecimal federalTax,
    BigDecimal medicare,
    BigDecimal socialSecurity,
    BigDecimal stateTax,
    BigDecimal retirement401k,
    BigDecimal healthcare
) {
    public BigDecimal getNetPay() {
        return earnings
                .subtract(federalTax)
                .subtract(medicare)
                .subtract(socialSecurity)
                .subtract(stateTax)
                .subtract(retirement401k)
                .subtract(healthcare);
    }
    
    public String getFormattedPayDate() {
        return payDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
}