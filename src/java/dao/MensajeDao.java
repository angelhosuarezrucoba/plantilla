/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bd.Conexion;
import chat.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Home
 */
public class MensajeDao {

    public static void insert(Usuario usuario, Usuario contacto, String mensaje) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.conectar();
            Timestamp now = new Timestamp(new Date().getTime());
            ps = con.prepareCall("insert into internal_chat set fecha=? , fecha_index=? , hora_index=hour(?) ,campana=?,usuario=?,usuario_name=?,contacto=?,contacto_name=?,tipo=?,mensaje=?");
            ps.setTimestamp(1, now);
            ps.setTimestamp(2, now);
            ps.setTimestamp(3, now);
            ps.setInt(4, usuario.getCampana());

            ps.setLong(5, usuario.getUsuario());
            ps.setString(6, usuario.getNombre());
            ps.setLong(7, contacto.getUsuario());
            ps.setString(8, contacto.getNombre());
            ps.setString(9, "OUT");
            ps.setString(10, mensaje);
            ps.execute();

            ps.setLong(5, contacto.getUsuario());
            ps.setString(6, contacto.getNombre());
            ps.setLong(7, usuario.getUsuario());
            ps.setString(8, usuario.getNombre());
            ps.setString(9, "IN");
            ps.setString(10, mensaje);
            ps.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Conexion.cerrarConexion(con);
        }
    }

    public static JSONArray getMessages(Usuario usuario, Usuario contacto) {
        JSONArray messages = new JSONArray();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.conectar();
            ps = con.prepareStatement("select * , time_to_sec(fecha) as fecha_ from internal_chat where usuario=? and contacto=? and fecha_index=date(now()) order by fecha");
            ps.setLong(1, usuario.getUsuario());
            ps.setLong(2, contacto.getUsuario());
            rs = ps.executeQuery();
            while(rs.next()){
                JSONObject message = new JSONObject();
                message.put("mensaje", rs.getString("mensaje"));
                message.put("tipo", rs.getString("tipo"));
                message.put("usuario_name", rs.getString("usuario_name"));
                message.put("usuario", rs.getInt("usuario"));
                message.put("contacto_name", rs.getString("contacto_name"));
                message.put("contacto", rs.getInt("contacto"));
                message.put("fecha", rs.getString("fecha_"));
                messages.add(message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Conexion.cerrarConexion(con);
        }
        return messages;
    }

}
