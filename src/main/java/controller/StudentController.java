package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            // --- 5.2 NEW CODE STARTS HERE ---
            case "search":
                searchStudents(request, response);
                break;
            //7.2
            case "sort":
                sortStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    // List all students
    // 8.2: Pagination Controller Logic
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 2. Define recordsPerPage (typically 10)
        final int recordsPerPage = 10;
        int currentPage = 1;
        
        // 1. Get page parameter from request (default to 1 if not provided)
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                // Keep default currentPage = 1 if parameter is invalid
            }
        }
        
        // Edge case: Handle if page < 1 (default to 1)
        if (currentPage < 1) {
            currentPage = 1;
        }
        // Get total records first
        int totalRecords = studentDAO.getTotalStudents(); // Task 8.1 Method 1
        
        // 3. Calculate totalPages
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        
        // Edge case: Handle if page > totalPages (default to last page)
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        // 3. Calculate offset
        // Formula: offset = (currentPage - 1) * recordsPerPage
        int offset = (currentPage - 1) * recordsPerPage;
        
        List<Student> students;
        String action = request.getParameter("action");
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        String major = request.getParameter("major");
        
        // Default to Paginated Results (Task 8.1 Method 2)
        students = studentDAO.getStudentsPaginated(offset, recordsPerPage); // 4. Call DAO pagination methods
        
        
        // 5. Set multiple attributes: students, currentPage, totalPages
        request.setAttribute("students", students);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        
        // Set attributes for sticky Sort/Filter UI (if applicable)
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);
        request.setAttribute("selectedMajor", major);

        // 6. Forward to view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for new student
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for editing student
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    

    // Updated insertStudent with Validation (Exercise 6.2)
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student newStudent = new Student(studentCode, fullName, email, major);
        
        // 1. Call validation method
        if (validateStudent(newStudent, request)) {
            // VALID: Save to DB and Redirect
            if (studentDAO.addStudent(newStudent)) {
                response.sendRedirect("student?action=list&message=Student added successfully");
            } else {
                response.sendRedirect("student?action=list&error=Failed to add student");
            }
        } else {
            // INVALID:
            // 2. Set 'student' attribute to keep the input data (Sticky form)
            request.setAttribute("student", newStudent);
            
            // 3. Forward back to form to show errors
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    // Updated updateStudent with Validation (Exercise 6.2)
        private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {

            int id = Integer.parseInt(request.getParameter("id"));
            String studentCode = request.getParameter("studentCode");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String major = request.getParameter("major");

            Student student = new Student(studentCode, fullName, email, major);
            student.setId(id); // Important: Set the ID for update

            // 1. Call validation method
            if (validateStudent(student, request)) {
                // VALID: Update DB and Redirect
                if (studentDAO.updateStudent(student)) {
                    response.sendRedirect("student?action=list&message=Student updated successfully");
                } else {
                    response.sendRedirect("student?action=list&error=Failed to update student");
                }
            } else {
                // INVALID:
                // 2. Set 'student' attribute to keep the input data
                request.setAttribute("student", student);

                // 3. Forward back to form to show errors
                RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
                dispatcher.forward(request, response);
            }
        }
    
    // Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    // Requirement 5.2: Search logic
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get keyword parameter from request
        String keyword = request.getParameter("keyword");
        
        // 2. Handle null/empty keyword appropriately
        // If null (first load) or empty, we treat it as an empty string 
        // which matches all records in the LIKE query (%%)
        if (keyword == null) {
            keyword = "";
        }
        
        // 3. Call DAO's search method
        List<Student> students = studentDAO.searchStudents(keyword);
        
        // 4. Set BOTH students and keyword as request attributes
        request.setAttribute("students", students); // The list of results
        request.setAttribute("keyword", keyword);   // To keep the search term in the input box
        
        // 5. Forward to student-list.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // add validateStudent 6.1
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;
        
        // 1. Validate Student Code
        // Rule: Cannot be null/empty AND Must match pattern (2 letters + 3 digits)
        String codePattern = "[A-Z]{2}[0-9]{3,}";
        String studentCode = student.getStudentCode();
        
        if (studentCode == null || studentCode.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else if (!studentCode.matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 uppercase letters + 3+ digits (e.g., SV001)");
            isValid = false;
        }
        
        // 2. Validate Full Name
        // Rule: Cannot be null/empty AND Minimum length 2 characters
        String fullName = student.getFullName();
        
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            request.setAttribute("errorName", "Full name must be at least 2 characters");
            isValid = false;
        }
        
        // 3. Validate Email
        // Rule: If provided, must match email pattern
        String email = student.getEmail();
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        
        // Note: Form JSP has 'required', but here we validate format if string is not empty
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }
        
        // 4. Validate Major
        // Rule: Cannot be null or empty
        String major = student.getMajor();
        
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }
        
        return isValid;
    }
    
    // 7.2
    // Method 1: Handle Sort Action
    private void sortStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get parameters from URL (e.g., ?action=sort&sortBy=email&order=desc)
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        
        // 2. Call DAO method created in 7.1
        List<Student> students = studentDAO.getStudentsSorted(sortBy, order);
        
        // 3. Set attributes to display results AND keep the "sticky" state
        request.setAttribute("students", students);
        request.setAttribute("sortBy", sortBy); // 
        request.setAttribute("order", order);   // 
        
        // 4. Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // Method 2: Handle Filter Action
    private void filterStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get parameter from dropdown
        String major = request.getParameter("major");
        
        // 2. Call DAO method created in 7.1
        List<Student> students;
        if (major == null || major.trim().isEmpty()) {
            students = studentDAO.getAllStudents();
        } else {
            students = studentDAO.getStudentsByMajor(major);
        }
        
        // 3. Set attributes
        request.setAttribute("students", students);
        request.setAttribute("selectedMajor", major); // "Sticky" dropdown 
        
        // 4. Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
}
