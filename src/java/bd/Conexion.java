/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Home
 */
public class Conexion {
    
    public static Connection conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ccvox-io-2-0-0-0", "root", "netvox321x");
           // Connection con = DriverManager.getConnection("jdbc:mysql://192.168.10.207:3306/ccvox-io-4-0", "loader", "netvox321x");
            return con;
        } catch (SQLException ex) {
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }

    }
    
    public static void cerrarConexion(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
