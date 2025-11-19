package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "thinh123";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                students.add(student);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    // Get student by ID
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                return student;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new student
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update student
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET student_code = ?, full_name = ?, email = ?, major = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            pstmt.setInt(5, student.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete student
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Search students across code, name, and email
    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        
        // SQL query using LIKE operator for 3 columns with OR logic
        // Ordered by id descending as requested
        String sql = "SELECT * FROM students WHERE student_code LIKE ? OR full_name LIKE ? OR email LIKE ? ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Prepare the search pattern with wildcards (%)
            // If keyword is "Minh", pattern becomes "%Minh%"
            String searchPattern = "%" + keyword + "%";
            
            // Set the same pattern for all three placeholders (?)
            pstmt.setString(1, searchPattern); // For student_code
            pstmt.setString(2, searchPattern); // For full_name
            pstmt.setString(3, searchPattern); // For email
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentCode(rs.getString("student_code"));
                    student.setFullName(rs.getString("full_name"));
                    student.setEmail(rs.getString("email"));
                    student.setMajor(rs.getString("major"));
                    student.setCreatedAt(rs.getTimestamp("created_at"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Helper 1: Validate Column Name (Security)
    // Prevents SQL Injection since we can't use ? for column names
    private String validateSortBy(String sortBy) {
        String[] validColumns = {"id", "student_code", "full_name", "email", "major"};
        
        if (sortBy != null) {
            for (String col : validColumns) {
                if (col.equals(sortBy)) {
                    return col;
                }
            }
        }
        return "id"; // Default column
    }

    // Helper 2: Validate Order Direction
    private String validateOrder(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return "DESC";
        }
        return "ASC"; // Default order
    }

    // Method 1: Get Students Sorted (Requirement 7.1.1)
    public List<Student> getStudentsSorted(String sortBy, String order) {
        List<Student> students = new ArrayList<>();
        
        // 1. Validate inputs
        String validSortBy = validateSortBy(sortBy);
        String validOrder = validateOrder(order);
        
        // 2. Build SQL dynamically (Safe because we validated the strings above)
        String sql = "SELECT * FROM students ORDER BY " + validSortBy + " " + validOrder;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                students.add(student);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }

    // Method 2: Filter Students by Major (Requirement 7.1.2)
    public List<Student> getStudentsByMajor(String major) {
        List<Student> students = new ArrayList<>();
        
        // 1. SQL with WHERE clause
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 2. Set parameter
            pstmt.setString(1, major);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentCode(rs.getString("student_code"));
                    student.setFullName(rs.getString("full_name"));
                    student.setEmail(rs.getString("email"));
                    student.setMajor(rs.getString("major"));
                    student.setCreatedAt(rs.getTimestamp("created_at"));
                    students.add(student);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    // Method 1: Get Total Count
    // Executes: SELECT COUNT(*) FROM students
    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        int count = 0;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                // Return the count as integer
                count = rs.getInt(1); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    // Method 2: Get Paginated Results
    // Signature: public List<Student> getStudentsPaginated(int offset, int limit)
    public List<Student> getStudentsPaginated(int offset, int limit) {
        List<Student> students = new ArrayList<>();
        
        // Use SQL: SELECT * FROM students ORDER BY id DESC LIMIT ? OFFSET ?
        // Note: MySQL requires LIMIT before OFFSET
        String sql = "SELECT * FROM students ORDER BY id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // limit: number of records (e.g., 10)
            pstmt.setInt(1, limit);
            // offset: starting position (e.g., 0, 10, 20)
            pstmt.setInt(2, offset); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentCode(rs.getString("student_code"));
                    student.setFullName(rs.getString("full_name"));
                    student.setEmail(rs.getString("email"));
                    student.setMajor(rs.getString("major"));
                    student.setCreatedAt(rs.getTimestamp("created_at"));
                    students.add(student);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
}
