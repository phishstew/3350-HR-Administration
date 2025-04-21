/*
 * EmployeeSearch.java – self‑contained demo of the “Search” feature
 * ─────────────────────────────────────────────────────────────────────────────
 * This single file includes:
 *   • SearchCriteria  – value object for optional filters
 *   • EmployeeDAO     – data‑access interface
 *   • EmployeeDAOImpl – JDBC implementation (prepared + dynamic SQL)
 *   • EmployeeService – thin service layer used by UI
 *   • SearchEmployeeController – JavaFX controller (admin + employee modes)
 *     (Assume a companion SearchEmployee.fxml with TextFields, TableView, etc.)
 *
 * Replace the dummy DataSource with your own (HikariCP, Apache DBCP, etc.).
 */

package com.companyz.ems.search;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;
import javax.sql.DataSource;

// ─────────────────────────────────────────────────────────────────────────────
// Search criteria value object (all fields nullable / optional)
// ─────────────────────────────────────────────────────────────────────────────
public record SearchCriteria(Integer empId,
                             String firstName,
                             String lastName,
                             LocalDate dob,
                             String ssnPartial) { }

// ─────────────────────────────────────────────────────────────────────────────
// DAO interface
// ─────────────────────────────────────────────────────────────────────────────
interface EmployeeDAO {
    Optional<Employee> findByEmpId(int empId) throws SQLException;
    List<Employee> search(SearchCriteria criteria) throws SQLException;
}

// ─────────────────────────────────────────────────────────────────────────────
// JDBC implementation
// ─────────────────────────────────────────────────────────────────────────────
class EmployeeDAOImpl implements EmployeeDAO {
    private final DataSource ds;
    EmployeeDAOImpl(DataSource ds) { this.ds = ds; }

    @Override
    public Optional<Employee> findByEmpId(int empId) throws SQLException {
        SearchCriteria crit = new SearchCriteria(empId, null, null, null, null);
        return search(crit).stream().findFirst();
    }

    @Override
    public List<Employee> search(SearchCriteria c) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT e.empid, e.first_name, e.last_name, e.base_salary, " +
                "a.street, ci.city_name, st.state_abbr, a.zip, a.dob, a.mobile " +
                "FROM employees e " +
                "LEFT JOIN addresses a   ON e.empid = a.empid " +
                "LEFT JOIN cities ci     ON a.city_id = ci.city_id " +
                "LEFT JOIN states st     ON ci.state_id = st.state_id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (c.empId() != null)       { sql.append("AND e.empid = ? "); params.add(c.empId()); }
        if (c.firstName() != null)   { sql.append("AND e.first_name LIKE ? "); params.add(c.firstName() + "%"); }
        if (c.lastName() != null)    { sql.append("AND e.last_name LIKE ? ");  params.add(c.lastName() + "%"); }
        if (c.dob() != null)         { sql.append("AND a.dob = ? ");          params.add(Date.valueOf(c.dob())); }
        if (c.ssnPartial() != null)  { sql.append("AND e.ssn LIKE ? ");       params.add("%" + c.ssnPartial()); }

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Employee> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getInt("empid"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getBigDecimal("base_salary"),
            rs.getString("street"),
            rs.getString("city_name"),
            rs.getString("state_abbr"),
            rs.getString("zip"),
            rs.getDate("dob").toLocalDate(),
            rs.getString("mobile"));
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Plain POJO used by UI layer (trimmed fields for brevity)
// ─────────────────────────────────────────────────────────────────────────────
record Employee(int empId, String firstName, String lastName, java.math.BigDecimal salary,
                String street, String city, String state, String zip,
                LocalDate dob, String mobile) { }

// ─────────────────────────────────────────────────────────────────────────────
// Service layer (transaction/ag‑nostic)
// ─────────────────────────────────────────────────────────────────────────────
class EmployeeService {
    private final EmployeeDAO dao;
    EmployeeService(EmployeeDAO dao) { this.dao = dao; }

    public List<Employee> searchEmployees(SearchCriteria criteria) {
        try {
            return dao.search(criteria);
        } catch (SQLException ex) {
            throw new RuntimeException("Search failed", ex);
        }
    }

    public Optional<Employee> getEmployee(int empId) {
        try {
            return dao.findByEmpId(empId);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// JavaFX Controller (FXML id = "searchView")
// ─────────────────────────────────────────────────────────────────────────────

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.StringConverter;

class SearchEmployeeController {
    @FXML private TextField empIdField, firstNameField, lastNameField, ssnField;
    @FXML private DatePicker dobPicker;
    @FXML private TableView<Employee> table;

    private final EmployeeService service;
    private final boolean isAdmin;

    // Dependency injection via constructor or setter
    SearchEmployeeController(EmployeeService service, boolean isAdmin) {
        this.service = service;
        this.isAdmin = isAdmin;
    }

    @FXML
    private void initialize() {
        // Configure DatePicker to show proper format
        dobPicker.setConverter(new StringConverter<>() {
            @Override public String toString(LocalDate d) { return d == null ? "" : d.toString(); }
            @Override public LocalDate fromString(String s) { return (s == null || s.isEmpty()) ? null : LocalDate.parse(s); }
        });

        // TableView columns can be defined in FXML or here programmatically
        // e.g., new TableColumn<Employee, String>("First Name").setCellValueFactory(c -> new SimpleStringProperty(c.getValue().firstName()));

        // Double‑click row to open edit dialog (Admin only)
        table.setRowFactory(tv -> {
            TableRow<Employee> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
                    Employee emp = row.getItem();
                    if (isAdmin) EditEmployeeDialog.show(emp.getEmpId());
                }
            });
            return row;
        });
    }

    @FXML
    private void handleSearch() {
        SearchCriteria criteria = new SearchCriteria(
            parseIntOrNull(empIdField.getText()),
            emptyToNull(firstNameField.getText()),
            emptyToNull(lastNameField.getText()),
            dobPicker.getValue(),
            emptyToNull(ssnField.getText())
        );
        List<Employee> result = service.searchEmployees(criteria);
        table.setItems(FXCollections.observableArrayList(result));
    }

    // Utility helpers
    private static Integer parseIntOrNull(String s) { try { return (s==null||s.isBlank())?null:Integer.valueOf(s); } catch(NumberFormatException e){ return null;} }
    private static String emptyToNull(String s) { return (s==null||s.isBlank())?null:s; }
}

// ─────────────────────────────────────────────────────────────────────────────
// (Optional) stub for modal edit dialog
// ─────────────────────────────────────────────────────────────────────────────
class EditEmployeeDialog {
    static void show(int empId) {
        // Implementation left to your team
    }
}
