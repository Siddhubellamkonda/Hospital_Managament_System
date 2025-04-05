import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ManageAppointmentsServlet")
public class ManageAppointmentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("addAppointment".equals(action)) {
            addAppointment(request, response);
        } else if ("deleteAppointment".equals(action)) {
            deleteAppointment(request, response);
        }
    }
    
    private void addAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int patientId = Integer.parseInt(request.getParameter("patient_id"));
        int doctorId = Integer.parseInt(request.getParameter("doctor_id"));
        String appointmentDate = request.getParameter("appointment_date");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, appointmentDate);
            
            ps.executeUpdate();
            con.close();
            response.sendRedirect("manage_appointments.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void deleteAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String appointmentIdStr = request.getParameter("appointment_id");
        if (appointmentIdStr == null || appointmentIdStr.isEmpty()) {
            response.sendRedirect("manage_appointments.html");
            return;
        }
        
        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "DELETE FROM appointments WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            
            con.close();
            response.sendRedirect("manage_appointments.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
