
STUDENT INFORMATION:
Name: Huynh Trinh Phuc Thinh
Student ID: ITCSIU22230
Class: Web Application Development_S1_2025-26_G01_lab05

COMPLETED EXERCISES:
[x] Exercise 5: Search
[x] Exercise 6: Validation
[x] Exercise 7: Sorting & Filtering
[x] Exercise 8: Pagination

MVC COMPONENTS:
- Model: Student.java
- DAO: StudentDAO.java
- Controller: StudentController.java
- Views: student-list.jsp, student-form.jsp

FEATURES IMPLEMENTED:
- All CRUD operations
- Search functionality
- Server-side validation
- Sorting by columns
- Filter by major

KNOWN ISSUES:
No Duplicate Check: Email uniqueness is not verified against the database during insertion/update.
Hard Delete: Records are permanently deleted immediately; no "soft delete" or recovery option.
Concurrency: No handling for simultaneous updates (e.g., if two admins edit the same student at once).

EXTRA FEATURES:
Integrated Features: Search, Filter, Sort, and Pagination work together seamlessly (Sticky Parameters preserve state when navigating).
Sortable Headers: Clickable table headers with directional icons (⬆️/⬇️) to toggle ASC/DESC sorting.
Sticky Forms: Preserves user input in form fields when server-side validation fails.
Regex Validation: Implemented strict Server-Side validation patterns for Student Code and Email format.

TIME SPENT: 6h

REFERENCES USED:
- Gemini , W3 Schools, Chatgpt, Youtube.