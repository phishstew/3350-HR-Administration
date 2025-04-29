import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class EmployeeDAOImpl implements EmployeeDAO {

    // Helper method to map ResultSet row to Employee object
    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getString("emp_id")); // Adjust column names as per your DB schema
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        emp.setDateOfBirth(rs.getObject("dob", LocalDate.class)); // Requires JDBC 4.2+ driver
        emp.setSsn(rs.getString("ssn"));
        emp.setSalary(rs.getBigDecimal("salary"));
        emp.setJobTitle(rs.getString("job_title"));
        emp.setDivision(rs.getString("division"));
        // Map other fields...
        return emp;
    }

    @Override
    public Employee findEmployeeById(String employeeId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM employees WHERE emp_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmployee(rs);
                } else {
                    return null; // Or throw a NotFoundException
                }
            }
        }
    }

    @Override
    public Employee findEmployeeBySsn(String ssn, Connection conn) throws SQLException {
         // Similar implementation to findEmployeeById, but query WHERE ssn = ?
         String sql = "SELECT * FROM employees WHERE ssn = ?";
         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, ssn);
             try (ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     return mapRowToEmployee(rs);
                 } else {
                     return null;
                 }
             }
         }
    }

     @Override
    public Employee findEmployeeByEmpId(String empId, Connection conn) throws SQLException {
         // This is identical to findEmployeeById if emp_id is the primary key / employee ID field
         return findEmployeeById(empId, conn);
    }


    @Override
    public List<Employee> findEmployeesByName(String name, Connection conn) throws SQLException {
        // Using LIKE for partial matching on first or last name
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE first_name LIKE ? OR last_name LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likePattern = "%" + name + "%";
            pstmt.setString(1, likePattern);
            pstmt.setString(2, likePattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRowToEmployee(rs));
                }
            }
        }
        return employees;
    }

     @Override
    public List<Employee> findEmployeesByDob(LocalDate dob, Connection conn) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE dob = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, dob); // Requires JDBC 4.2+ driver
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRowToEmployee(rs));
                }
            }
        }
        return employees;
    }

    /**
     * Generic search implementation for Admins.
     * IMPORTANT: Ensure searchField corresponds to a valid, safe column name
     * to prevent SQL injection if constructing SQL dynamically. Using specific
     * methods (like findByName, findBySsn) is generally safer.
     * This example assumes specific methods are preferred based on UI selection.
     */
    @Override
    public List<Employee> findEmployeesByCriteria(String searchField, String searchValue, Connection conn) throws SQLException {
        // This method would delegate to the specific methods based on searchField
        // Or construct dynamic SQL CAREFULLY (preferably avoid)
        // Example delegation:
        switch (searchField.toLowerCase()) {
            case "name":
                return findEmployeesByName(searchValue, conn);
            case "ssn":
                Employee empBySsn = findEmployeeBySsn(searchValue, conn);
                List<Employee> resultSsn = new ArrayList<>();
                if (empBySsn != null) resultSsn.add(empBySsn);
                return resultSsn;
            case "empid":
                 Employee empById = findEmployeeByEmpId(searchValue, conn);
                 List<Employee> resultId = new ArrayList<>();
                 if (empById != null) resultId.add(empById);
                 return resultId;
            case "dob":
                 try {
                     LocalDate dob = LocalDate.parse(searchValue); // Basic parsing, add error handling
                     return findEmployeesByDob(dob, conn);
                 } catch (Exception e) {
                     // Handle invalid date format
                     System.err.println("Invalid date format for DOB search: " + searchValue);
                     return new ArrayList<>(); // Return empty list on error
                 }
            default:
                System.err.println("Unsupported search field: " + searchField);
                return new ArrayList<>(); // Return empty list for unsupported fields
        }
    }

    // Implement other DAO methods (update, create, delete) here...
    /*
    public int updateSalariesByRange(double percentIncrease, double minSalary, double maxSalary) {
        String sql = "UPDATE employees SET salary = salary * ? WHERE salary >= ? AND salary < ?";
        int rowsUpdated = 0;
    
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            double multiplier = 1 + (percentIncrease / 100.0);
    
            stmt.setDouble(1, multiplier);
            stmt.setDouble(2, minSalary);
            stmt.setDouble(3, maxSalary);
    
            rowsUpdated = stmt.executeUpdate();
            System.out.println("Updated " + rowsUpdated + " employee salaries.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return rowsUpdated;
    }
    public boolean addEmployee(Employee newEmp) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO employees (first_name, last_name, ssn, dob, hire_date, salary) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newEmp.getFirstName());
            stmt.setString(2, newEmp.getLastName());
            stmt.setString(3, newEmp.getSsn());
            stmt.setDate(4, java.sql.Date.valueOf(newEmp.getDob()));
            stmt.setDate(5, java.sql.Date.valueOf(newEmp.getHireDate()));
            stmt.setDouble(6, newEmp.getSalary());
    
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Employee getEmployeeById(int empId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM employees WHERE empid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return new Employee(
                    rs.getInt("empid"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("ssn"),
                    rs.getDate("dob").toLocalDate(),
                    rs.getDate("hire_date").toLocalDate(),
                    rs.getDouble("salary")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean updateEmployee(Employee emp) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE employees SET first_name = ?, last_name = ?, ssn = ?, dob = ?, hire_date = ?, salary = ? WHERE empid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, emp.getFirstName());
            stmt.setString(2, emp.getLastName());
            stmt.setString(3, emp.getSsn());
            stmt.setDate(4, java.sql.Date.valueOf(emp.getDob()));
            stmt.setDate(5, java.sql.Date.valueOf(emp.getHireDate()));
            stmt.setDouble(6, emp.getSalary());
            stmt.setInt(7, emp.getEmpId());
    
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteEmployee(int empId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM employees WHERE empid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
    
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
*/
}
