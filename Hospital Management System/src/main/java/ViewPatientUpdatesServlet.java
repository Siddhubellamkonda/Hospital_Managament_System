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

@WebServlet("/ViewPatientUpdatesServlet")
public class ViewPatientUpdatesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "12345");

            // Query to fetch patient updates
            String query = "SELECT * FROM patient_updates ORDER BY update_date DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Display updates in table format
            StringBuilder htmlTable = new StringBuilder();
            while (rs.next()) {
                htmlTable.append("<tr>");
                htmlTable.append("<td>").append(rs.getInt("patient_id")).append("</td>");
                htmlTable.append("<td>").append(rs.getInt("doctor_id")).append("</td>");
                htmlTable.append("<td>").append(rs.getTimestamp("update_date")).append("</td>");
                htmlTable.append("<td>").append(rs.getString("updated_disease")).append("</td>");
                htmlTable.append("<td>").append(rs.getString("status")).append("</td>");
                htmlTable.append("</tr>");
            }

            out.println(htmlTable.toString());
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='5'>Error retrieving updates: " + e.getMessage() + "</td></tr>");
        }
    }
}
