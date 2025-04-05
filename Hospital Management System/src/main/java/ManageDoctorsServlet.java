import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ManageDoctorsServlet")
public class ManageDoctorsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("addDoctor".equals(action)) {
            addDoctor(request, response);
        } else if ("deleteDoctor".equals(action)) {
            deleteDoctor(request, response);
        }
    }
    
    private void addDoctor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String specialization = request.getParameter("specialization");
        String phone = request.getParameter("phone");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "INSERT INTO doctors (name, specialization, phone) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setString(3, phone);
            
            ps.executeUpdate();
            con.close();
            response.sendRedirect("manage_doctors.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void deleteDoctor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String doctorIdStr = request.getParameter("doctorId");
        if (doctorIdStr == null || doctorIdStr.isEmpty()) {
            response.sendRedirect("manage_doctors.html");
            return;
        }
        
        try {
            int doctorId = Integer.parseInt(doctorIdStr);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");
            
            String query = "DELETE FROM doctors WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, doctorId);
            ps.executeUpdate();
            
            con.close();
            response.sendRedirect("manage_doctors.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
