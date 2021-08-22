import java.sql.*;

public class OrderManager {

    public static void main(String[] args) {
            Connection c = null;
            Statement stmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:orders.db");             // create a connection to the database

                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS ORDERLIST " +          // SQL statement for creating a new table

                        "(ID INT PRIMARY KEY ," +
                        " ORIGIN TEXT NOT NULL, " +
                        " DESTINATION TEXT NOT NULL, " +
                        " TAKEN INT NOT NULL )" ;
                stmt.executeUpdate(sql);             // create a new table


                // TO DROP THE TABLE
                /*
                c.setAutoCommit(false);
                Statement stmt1 = c.createStatement();
                String sqlCommand = "DROP TABLE 'orderlist' ";
                stmt1.executeUpdate(sqlCommand);
                c.commit();
                */

                stmt.close();
                c.close();

            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println("Table created successfully");
            //list_orders();

        switch(args.length){
            case 0 :break;
            default:
                switch(args[0]){
                    case "create_order": create_order(args[1],args[2]);break;
                    case "list_orders": list_orders(); break;
                    case "take_order":take_order(Integer.parseInt(args[1]));break;
                    default:break;
            }

        }
    }

    static void create_order(String origin, String dest) {
        Connection c;
        Statement stmt;
        int id;
        if(origin == "" || dest == ""){
            System.out.println("Origin or destination is not provided."); //parameter should be provided
            return;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:orders.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            String query = "SELECT * FROM orderlist";
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getString(1) + "|" + rs.getString(2) + "|" +
                        rs.getString(3)+ "|" +rs.getString(4));
            }

            rs = stmt.executeQuery("SELECT MAX(id) FROM orderlist"); //get the latest order ID
            id = rs.getInt(1);

            if(!rs.next()) id = 1; //create ID for new order
            else id += 1;

            String sql = "INSERT INTO orderlist (id, origin, destination, taken) VALUES (?,?,?,?);"; //SQL statement to add new order into table

            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1,id);
            pstmt.setString(2,origin);
            pstmt.setString(3,dest);
            pstmt.setInt(4,0);
            pstmt.executeUpdate();

            pstmt.close();
            stmt.close();
            c.commit();
            c.close();

            System.out.println(id); //print ID

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    static void list_orders(){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:orders.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            String query = "SELECT * FROM orderlist WHERE taken = 0"; //find those available(not taken) orders
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(query); // make a query

            while (rs.next()) {
                System.out.println(rs.getString(1) + "," + rs.getString(2) + "," + // print orders
                        rs.getString(3));
            }

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    static void take_order(int id){
        if(id == 0){
            System.out.println("id is not provided."); //parameter should be provided
            return;
        }
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:orders.db");
            c.setAutoCommit(false);

            String query = "SELECT * FROM orderlist WHERE id = ?"; //find the order based on the id provided
            PreparedStatement pstmt = c.prepareStatement(query);
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            //c.commit();
            if(!rs.next()){                                           //order not exist
                System.out.println("order does not exist");
            }
            else if(rs.getInt(4) == 1){                 //order taken
                System.out.println("order already taken");
            }
            else{
            String sql = "UPDATE orderlist SET taken = 1  "    //update the order to be taken
                    + "WHERE id = ?";
            pstmt = c.prepareStatement(sql);
            pstmt.setInt(1,id);
            pstmt.executeUpdate();
            }

            c.commit();
            pstmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}



