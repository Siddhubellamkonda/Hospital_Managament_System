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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Fetch login details
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // Validate user input
        if (username == null || password == null || role == null || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            out.println("Error: All fields are required.");
            return;
        }

        try {
            // Database Connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");

            String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("role", role);

                // If doctor logs in, fetch their doctor ID
                if ("doctor".equals(role)) {
                    int doctorId = getDoctorId(con, username);
                    session.setAttribute("doctor_id", doctorId);
                }

                // Redirect based on role
                if ("admin".equals(role)) {
                    response.sendRedirect("admin_dashboard.html");
                } else if ("doctor".equals(role)) {
                    response.sendRedirect("doctor_dashboard.html");
                }
            } else {
                out.println("Invalid Credentials. Please try again.");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Database Connection Error: " + e.getMessage());
        }
    }

    private int getDoctorId(Connection con, String username) throws Exception {
        int doctorId = -1;
        String query = "SELECT id FROM doctors WHERE name=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            doctorId = rs.getInt("id");
        }
        return doctorId;
    }
}
