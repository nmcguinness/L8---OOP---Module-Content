package assessments.gca.gca2.db;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testng.AssertJUnit.assertNotNull;

class DatabaseConnectionTest {

    @Test
    void open_Successfully() throws SQLException {

        //ACTIVATE
         String URL     = "jdbc:mysql://localhost:3306/gca2_support_db";
         String DB_USER = "root";
         String DB_PASS = "";

        //ACT
        DatabaseConnection db = new DatabaseConnection(URL, DB_USER, DB_PASS);
        Connection conn = db.open();

        //ASSERT
        assertNotNull(conn);

        conn.close();


    }

    @Test
    void open_Failure_Wrong_Credentials() throws SQLException {

        //ACTIVATE
        String URL     = "jdbc:mysql://localhost:3306/gca2_support_db_not_there";
        String DB_USER = "root";
        String DB_PASS = "";

        //ACT
        DatabaseConnection db = new DatabaseConnection(URL, DB_USER, DB_PASS);
        Connection conn = db.open();

        //ASSERT
        assertNotNull(conn);

        conn.close();


    }
}