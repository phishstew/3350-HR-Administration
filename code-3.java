import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

public interface EmployeeDAO {
    // For Admin search - flexible search
    List<Employee> findEmployeesByCriteria(String searchField, String searchValue, Connection conn) throws SQLException;

    // Specific searches might be useful too
    List<Employee> findEmployeesByName(String name, Connection conn) throws SQLException;
    Employee findEmployeeBySsn(String ssn, Connection conn) throws SQLException;
    Employee findEmployeeByEmpId(String empId, Connection conn) throws SQLException;
    List<Employee> findEmployeesByDob(LocalDate dob, Connection conn) throws SQLException; // DOB might not be unique

    // For Employee self-view
    Employee findEmployeeById(String employeeId, Connection conn) throws SQLException;

    // Other methods needed for CRUD (Update, Delete, Create) would go here
    // void updateEmployee(Employee employee, Connection conn) throws SQLException;
    // ... etc
}