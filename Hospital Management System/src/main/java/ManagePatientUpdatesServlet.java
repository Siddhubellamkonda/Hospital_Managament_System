import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ManagePatientUpdatesServlet")
public class ManagePatientUpdatesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        if ("addUpdate".equals(action)) {
            addPatientUpdate(request, response, out);
        } else if ("viewUpdates".equals(action)) {
            viewPatientUpdates(request, response, out);
        }
    }

    private void addPatientUpdate(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String patientIdStr = request.getParameter("patient_id");
        String disease = request.getParameter("disease");
        String status = request.getParameter("status");

        // Get doctor ID from session
        HttpSession session = request.getSession();
        Integer doctorId = (Integer) session.getAttribute("doctor_id");

        if (patientIdStr == null || patientIdStr.isEmpty() || 
            disease == null || disease.isEmpty() || 
            status == null || status.isEmpty() || 
            doctorId == null) {
            out.println("<h3>Error: Missing input data or doctor not logged in.</h3>");
            return;
        }

        try {
            int patientId = Integer.parseInt(patientIdStr);

            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");

            // Insert update into patient_updates table
            String insertQuery = "INSERT INTO patient_updates (patient_id, doctor_id, updated_disease, status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, disease);
            ps.setString(4, status);
            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                out.println("<h3>Patient update recorded successfully!</h3>");
            } else {
                out.println("<h3>Error: Data not inserted.</h3>");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Database Error: " + e.getMessage() + "</h3>");
        }
    }

    private void viewPatientUpdates(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String patientIdStr = request.getParameter("patient_id");

        if (patientIdStr == null || patientIdStr.isEmpty()) {
            out.println("<h3>Error: Patient ID is required.</h3>");
            return;
        }

        try {
            int patientId = Integer.parseInt(patientIdStr);

            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");

            // Retrieve updates from patient_updates table
            String query = "SELECT * FROM patient_updates WHERE patient_id = ? ORDER BY update_date DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            out.println("<h3>Patient Update History</h3>");
            out.println("<table border='1'><tr><th>Update ID</th><th>Doctor ID</th><th>Date</th><th>Disease</th><th>Status</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("update_id") + "</td>");
                out.println("<td>" + rs.getInt("doctor_id") + "</td>");
                out.println("<td>" + rs.getTimestamp("update_date") + "</td>");
                out.println("<td>" + rs.getString("updated_disease") + "</td>");
                out.println("<td>" + rs.getString("status") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error retrieving patient updates: " + e.getMessage() + "</h3>");
        }
    }
}
