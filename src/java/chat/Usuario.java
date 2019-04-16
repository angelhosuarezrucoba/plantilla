/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.ArrayList;
import java.util.HashSet;
import javax.websocket.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Home
 */
public class Usuario {

    /**
     * @return the campanas
     */
    public ArrayList<Integer> getCampanas() {
        return campanas;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    private long usuario = 0;
    private Perfil perfil;
    private int campana = 0;
    private String nombre = "";
    private Session session = null;
    private ArrayList<Integer> campanas = new ArrayList<>();

    public Usuario(JSONObject datos, Session session) throws Exception {
        this.usuario = Long.parseLong(datos.get("usuario").toString());
        this.perfil = Perfil.valueOf(datos.get("perfil").toString());
        this.campana = Integer.parseInt(datos.get("campana").toString());
        this.nombre = datos.get("nombre").toString();
        this.session = session;
        if (datos.get("campanas") != null) {
            JSONArray campanas_ = (JSONArray) new JSONParser().parse(datos.get("campanas").toString());
            for (Object campana_ : campanas_) {
                campanas.add(Integer.parseInt(campana_.toString()));
            }
        }

        System.out.println(this.toString());
    }

    /**
     * @return the usuario
     */
    public long getUsuario() {
        return usuario;
    }

    /**
     * @return the perfil
     */
    public Perfil getPerfil() {
        return perfil;
    }

    /**
     * @return the campana
     */
    public int getCampana() {
        return campana;
    }

    public boolean getCampana(int campana) {
        for (int campana_ : getCampanas()) {
            if (campana_ == campana) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "Usuario{" + "usuario=" + usuario + ", perfil=" + perfil + ", campana=" + campana + ", nombre=" + nombre + ", session=" + session + ", campanas=" + getCampanas() + '}';
    }

}
