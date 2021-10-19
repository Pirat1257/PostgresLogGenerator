import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*---- Работа с базой данных postgres ----*/
public class DB_work {

    private String url; // url to DB
    private Connection con_admin; // Connection to DB for admin
    private Statement st_admin; // Statement object for sending SQL statements to the database for admin

    public boolean connect_to_BD(String new_url, String admin_username, String admin_password) {
        try {
            con_admin = DriverManager.getConnection(new_url, admin_username, admin_password);
            st_admin = con_admin.createStatement();
            url = new_url;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
