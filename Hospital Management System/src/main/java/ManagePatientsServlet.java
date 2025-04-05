import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ManagePatientsServlet")
public class ManagePatientsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("addPatient".equals(action)) {
            addPatient(request, response);
        } else if ("deletePatient".equals(action)) {
            deletePatient(request, response);
        }
    }
    
    private void addPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        int age = Integer.parseInt(request.getParameter("age"));
        String gender = request.getParameter("gender");
        String phone = request.getParameter("phone");
        String disease = request.getParameter("disease");
        int doctorId = Integer.parseInt(request.getParameter("doctor_id"));
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "INSERT INTO patients (name, age, gender, phone, disease, doctor_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, disease);
            ps.setInt(6, doctorId);
            
            ps.executeUpdate();
            con.close();
            response.sendRedirect("manage_patients.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void deletePatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String patientIdStr = request.getParameter("patientId");
        if (patientIdStr == null || patientIdStr.isEmpty()) {
            response.sendRedirect("manage_patients.html");
            return;
        }
        
        try {
            int patientId = Integer.parseInt(patientIdStr);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "DELETE FROM patients WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, patientId);
            ps.executeUpdate();
            
            con.close();
            response.sendRedirect("manage_patients.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
