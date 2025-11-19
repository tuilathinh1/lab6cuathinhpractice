<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-style: italic;
        }
        
        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-weight: 500;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .btn {
            display: inline-block;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
            padding: 8px 16px;
            font-size: 13px;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }
        
        tbody tr {
            transition: background-color 0.2s;
        }
        
        tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .actions {
            display: flex;
            gap: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
        
        /*add css 5.3*/
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .search-form {
            display: flex;
            gap: 10px;
        }
        .search-input {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            width: 250px;
        }
        /*add css 8.3*/
        .pagination-link {
            text-decoration: none; 
            padding: 8px 12px; 
            border: 1px solid #ddd; 
            border-radius: 5px; 
            color: #667eea;
            background-color: white;
        }
        .pagination-active {
            font-weight: bold;
            color: white; 
            background-color: #667eea;
            border-color: #667eea;
        }
        .pagination-disabled {
            color: #ccc; 
            background-color: #f5f5f5;
            cursor: not-allowed;
            pointer-events: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📚 Student Management System</h1>
        <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>
        
        <!-- Success Message -->
        <c:if test="${not empty param.message}">
            <div class="message success">
                ✅ ${param.message}
            </div>
        </c:if>
        
        <!-- Error Message -->
        <c:if test="${not empty param.error}">
            <div class="message error">
                ❌ ${param.error}
            </div>
        </c:if>
        
        <!-- Add New Student Button -->
        <div class="toolbar" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <a href="student?action=new" class="btn btn-primary">
                ➕ Add New Student
            </a>
            <!-- Add Filter -->
            <form action="student" method="GET">
                <input type="hidden" name="action" value="filter">
                <select name="major" class="filter-select" onchange="this.form.submit()">
                    <option value="">--- Filter by Major ---</option>
                    <option value="Computer Science" ${selectedMajor == 'Computer Science' ? 'selected' : ''}>
                        Computer Science
                    </option>
                    <option value="Information Technology" ${selectedMajor == 'Information Technology' ? 'selected' : ''}>
                        Information Technology
                    </option>
                    <option value="Software Engineering" ${selectedMajor == 'Software Engineering' ? 'selected' : ''}>
                        Software Engineering
                    </option>
                    <option value="Business Administration" ${selectedMajor == 'Business Administration' ? 'selected' : ''}>
                        Business Administration
                    </option>
                </select>
            </form>
            <!-- Add Search -->
            <form action="student" method="GET" style="display: flex; gap: 10px;">

                <input type="hidden" name="action" value="search">

                <input type="text" 
                       name="keyword" 
                       placeholder="Enter name, email..." 
                       value="${keyword}" 
                       style="padding: 10px; border: 1px solid #ddd; border-radius: 5px; width: 250px;">

                <button type="submit" class="btn btn-secondary">
                    🔍 Search
                </button>

                <c:if test="${not empty keyword}">
                    <a href="student?action=list" class="btn btn-danger" style="display: flex; align-items: center;">
                        Clear
                    </a>
                </c:if>
            </form>
        </div>

        <c:if test="${not empty keyword}">
            <div style="margin-bottom: 15px; padding: 10px; background-color: #e9ecef; border-radius: 5px; color: #495057;">
                ℹ️ Search results for: <strong>"${keyword}"</strong>
            </div>
        </c:if>
        
        <!-- Student Table -->
        <c:choose>
            <c:when test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <th>
                                <a href="student?action=sort&sortBy=id&order=${sortBy == 'id' && order == 'asc' ? 'desc' : 'asc'}" 
                                   style="color: white; text-decoration: none;">
                                    ID ${sortBy == 'id' ? (order == 'asc' ? '⬆️' : '⬇️') : ''}
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=student_code&order=${sortBy == 'student_code' && order == 'asc' ? 'desc' : 'asc'}"
                                   style="color: white; text-decoration: none;">
                                    Student Code ${sortBy == 'student_code' ? (order == 'asc' ? '⬆️' : '⬇️') : ''}
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=full_name&order=${sortBy == 'full_name' && order == 'asc' ? 'desc' : 'asc'}"
                                   style="color: white; text-decoration: none;">
                                    Full Name ${sortBy == 'full_name' ? (order == 'asc' ? '⬆️' : '⬇️') : ''}
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=email&order=${sortBy == 'email' && order == 'asc' ? 'desc' : 'asc'}"
                                   style="color: white; text-decoration: none;">
                                    Email ${sortBy == 'email' ? (order == 'asc' ? '⬆️' : '⬇️') : ''}
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=major&order=${sortBy == 'major' && order == 'asc' ? 'desc' : 'asc'}"
                                   style="color: white; text-decoration: none;">
                                    Major ${sortBy == 'major' ? (order == 'asc' ? '⬆️' : '⬇️') : ''}
                                </a>
                            </th>
                            
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.id}</td>
                                <td><strong>${student.studentCode}</strong></td>
                                <td>${student.fullName}</td>
                                <td>${student.email}</td>
                                <td>${student.major}</td>
                                <td>
                                    <div class="actions">
                                        <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">
                                            ✏️ Edit
                                        </a>
                                        <a href="student?action=delete&id=${student.id}" 
                                           class="btn btn-danger"
                                           onclick="return confirm('Are you sure you want to delete this student?')">
                                            🗑️ Delete
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                     <!--8.3-->
                    <c:if test="${totalPages > 1}">
                    <div class="pagination-container" style="display: flex; justify-content: center; margin-top: 30px; flex-direction: column; align-items: center; gap: 10px;">
                        <nav>
                            <ul class="pagination" style="list-style: none; padding: 0; margin: 0; display: flex; gap: 5px;">
                                
                                <li class="page-item">
                                    <a class="pagination-link ${currentPage <= 1 ? 'pagination-disabled' : ''}" 
                                       href="student?action=list&page=${currentPage - 1}&sortBy=${sortBy}&order=${order}&major=${selectedMajor}&keyword=${keyword}">
                                        &laquo; Previous
                                    </a>
                                </li>
                                
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item">
                                        <a class="pagination-link ${i == currentPage ? 'pagination-active' : ''}" 
                                           href="student?action=list&page=${i}&sortBy=${sortBy}&order=${order}&major=${selectedMajor}&keyword=${keyword}">
                                            ${i}
                                        </a>
                                    </li>
                                </c:forEach>
                                
                                <li class="page-item">
                                    <a class="pagination-link ${currentPage >= totalPages ? 'pagination-disabled' : ''}" 
                                       href="student?action=list&page=${currentPage + 1}&sortBy=${sortBy}&order=${order}&major=${selectedMajor}&keyword=${keyword}">
                                        Next &raquo;
                                    </a>
                                </li>
                                
                            </ul>
                        </nav>
                        
                        <div style="color: #666; font-size: 14px;">
                            Page ${currentPage} of ${totalPages}
                        </div>
                    </div>
                </c:if>
                     
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📭</div>
                    <h3>No students found</h3>
                    <p>Start by adding a new student</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
