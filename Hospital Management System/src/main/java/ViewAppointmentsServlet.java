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

@WebServlet("/ViewAppointmentsServlet")
public class ViewAppointmentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "SELECT * FROM appointments";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            
            out.println("<html><head><title>View Appointments</title></head><body>");
            out.println("<h2>Appointments List</h2>");
            out.println("<table border='1'><tr><th>Appointment ID</th><th>Patient ID</th><th>Doctor ID</th><th>Appointment Date</th><th>Status</th></tr>");
            
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getInt("patient_id") + "</td><td>" + rs.getInt("doctor_id") + "</td><td>" + rs.getString("appointment_date") + "</td><td>" + rs.getString("status") + "</td></tr>");
            }
            
            out.println("</table></body></html>");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error fetching appointments.</p>");
        }
    }
}
