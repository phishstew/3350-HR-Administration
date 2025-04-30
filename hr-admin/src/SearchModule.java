//package com.companyz.ems;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * Consolidated search‑layer implementation for the Employee Management System.
 * Drop this single file into your src/main/java tree.
 *
 * Usage (example):
 *     SearchModule.SearchService svc = new SearchModule.SearchService(SearchModule.Db.get());
 *     List<SearchModule.EmployeeRecord> r = svc.byEmpId(101);
 */
public class SearchModule {

    /* ---------------------------------------------------------
     * Data Transfer Object (record) returned by every search
     * --------------------------------------------------------- */
    public record EmployeeRecord(
            int empId,
            String firstName,
            String lastName,
            String ssn,
            LocalDate dob,
            String division,
            String jobTitle,
            java.math.BigDecimal salary) { }

    /* ---------------------------------------------------------
     * Lightweight connection‑pool wrapper (Apache DBCP)
     * --------------------------------------------------------- */
    public static final class Db {
        private static final DataSource DS;
        static {
            org.apache.commons.dbcp2.BasicDataSource b = new org.apache.commons.dbcp2.BasicDataSource();
            b.setDriverClassName("com.mysql.cj.jdbc.Driver");
            b.setUrl("jdbc:mysql://<host>:<port>/<database>?useSSL=false&serverTimezone=UTC");
            b.setUsername("<user>");
            b.setPassword("<password>");
            b.setInitialSize(3);
            DS = b;
        }
        public static DataSource get() { return DS; }
        private Db() { }
    }

    /* ---------------------------------------------------------
     * Core search logic – safe, parameterized SQL only
     * --------------------------------------------------------- */
    public static final class SearchService {
        private final DataSource dataSource;
        public SearchService(DataSource ds) { this.dataSource = ds; }

        /* Generic helper that maps a SELECT into EmployeeRecord objects */
        private List<EmployeeRecord> runQuery(String sql, java.util.function.Consumer<PreparedStatement> binder)
                throws SQLException {
            List<EmployeeRecord> out = new ArrayList<>();
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                binder.accept(ps);                  // bind parameters safely
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new EmployeeRecord(
                                rs.getInt("empid"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("ssn"),
                                rs.getDate("dob").toLocalDate(),
                                rs.getString("division"),
                                rs.getString("job_title"),
                                rs.getBigDecimal("salary")));
                    }
                }
            }
            return out;
        }

        /* Base SELECT reused by every method */
        private static final String BASE_SELECT = """
            SELECT e.empid,
                   e.first_name,
                   e.last_name,
                   e.ssn,
                   e.dob,
                   d.div_name   AS division,
                   jt.job_title AS job_title,
                   e.salary
            FROM   employees           e
            JOIN   employee_division   ed  ON e.empid = ed.empid
            JOIN   division            d   ON ed.div_id = d.div_id
            JOIN   employee_job_titles ejt ON e.empid = ejt.empid
            JOIN   job_titles          jt  ON ejt.job_title_id = jt.job_title_id
            WHERE  %s""";

        public List<EmployeeRecord> byEmpId(int empId) throws SQLException {
            String q = BASE_SELECT.formatted("e.empid = ?");
            return runQuery(q, ps -> {
                try {
                    ps.setInt(1, empId);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }

        public List<EmployeeRecord> bySsn(String ssn) throws SQLException {
            String q = BASE_SELECT.formatted("e.ssn = ?");
            return runQuery(q, ps -> {
                try {
                    ps.setString(1, ssn);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }

        public List<EmployeeRecord> byName(String nameLike) throws SQLException {
            String q = BASE_SELECT.formatted("CONCAT(e.first_name,' ',e.last_name) LIKE ?");
            return runQuery(q, ps -> {
                try {
                    ps.setString(1, "%" + nameLike + "%");
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }

        public List<EmployeeRecord> byDob(LocalDate dob) throws SQLException {
            String q = BASE_SELECT.formatted("e.dob = ?");
            return runQuery(q, ps -> {
                try {
                    ps.setDate(1, java.sql.Date.valueOf(dob));
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }
    }
}
