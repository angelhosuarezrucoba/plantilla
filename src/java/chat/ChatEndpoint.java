/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import dao.MensajeDao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Home
 */
@ServerEndpoint(value = "/chat")
public class ChatEndpoint {

    @OnOpen
    public void open(Session session) {
        try {
            System.out.println(new Date() + " Open Connection ..." + session.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @OnMessage
    public void onMessage(Session session, String messageJson) {
        JSONParser parser = new JSONParser();
        try {
            System.out.println(new Date() + " " + messageJson + " " + session.getId());
            JSONObject datos = (JSONObject) parser.parse(messageJson);
            Evento evento = Evento.valueOf(datos.get("evento").toString());
            JSONObject respuesta = new JSONObject();
            JSONArray usuarios = new JSONArray();
            int campana = 0;
            Usuario contacto = null;

            switch (evento) {
                case LOGIN:
                    Global.i_sessions.put(session.getId(), new Usuario(datos, session));
                    System.out.println(Global.i_sessions.size());
                    break;
                case MENSAJE:
                    long codigo_contacto = Long.parseLong(datos.get("contacto").toString());
                    String mensaje = datos.get("mensaje").toString();

                    Session session_contacto = getSession(codigo_contacto);
                    if (session_contacto != null) {
                        contacto = getContacto(session_contacto);
                        Usuario usuario = Global.i_sessions.get(session.getId());
                        respuesta.put("evento", evento.toString());
                        respuesta.put("mensaje", mensaje);
                        respuesta.put("usuario", usuario.getUsuario());
                        respuesta.put("nombre", usuario.getNombre());
                        System.out.println(new Date() + " respuesta : " + respuesta.toJSONString());
                        session_contacto.getAsyncRemote().sendText(respuesta.toJSONString());
                        MensajeDao.insert(usuario, contacto, mensaje);
                    }

                    break;
                case LISTA_AGENTES:
                    if (Global.i_sessions.containsKey(session.getId())) {
                        ArrayList<Integer> campanas = Global.i_sessions.get(session.getId()).getCampanas();
                        for (Integer campana_ : campanas) {
                            for (Usuario usuario : Global.i_sessions.values()) {
                                if (usuario.getCampana() == campana_ && usuario.getPerfil() == Perfil.AGENTE) {
                                    JSONObject agente = new JSONObject();
                                    agente.put("usuario", usuario.getUsuario());
                                    agente.put("nombre", usuario.getNombre());
                                    usuarios.add(agente);
                                }
                            }
                        }
                        respuesta.put("evento", evento.toString());
                        respuesta.put("usuarios", usuarios.toJSONString());
                        session.getAsyncRemote().sendText(respuesta.toJSONString());
                        System.out.println(respuesta);
                    }
                    break;
                case LISTA_SUPERVISORES:
                    campana = Integer.parseInt(datos.get("campana").toString());
                    JSONArray supervisores = new JSONArray();
                    for (Usuario usuario : Global.i_sessions.values()) {
                        if (usuario.getCampana(campana) && usuario.getPerfil() == Perfil.SUPERVISOR) {
                            JSONObject supervisor = new JSONObject();
                            supervisor.put("usuario", usuario.getUsuario());
                            supervisor.put("nombre", usuario.getNombre());
                            supervisores.add(supervisor);
                        }
                    }
                    respuesta.put("evento", evento.toString());
                    respuesta.put("usuarios", supervisores);
                    session.getAsyncRemote().sendText(respuesta.toJSONString());
                    System.out.println(respuesta.toJSONString());
                    break;

                case LISTA_MENSAJES:
                    codigo_contacto = Long.parseLong(datos.get("contacto").toString());
                    session_contacto = getSession(codigo_contacto);
                    if (session_contacto != null) {
                        contacto = getContacto(session_contacto);
                        Usuario usuario = Global.i_sessions.get(session.getId());
                        respuesta.put("evento", evento.toString());
                        respuesta.put("mensajes", MensajeDao.getMessages(usuario, contacto));
                        session.getAsyncRemote().sendText(respuesta.toJSONString());
                        System.out.println(respuesta.toJSONString());
                    }
                    break;

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Close Connection ... " + session.getId());
        if (Global.i_sessions.containsKey(session.getId())) {
            Usuario usuario_logout = Global.i_sessions.get(session.getId());
            if (usuario_logout.getPerfil() == Perfil.SUPERVISOR) {
                for (int campana : usuario_logout.getCampanas()) {
                    for (Usuario usuario_ : Global.i_sessions.values()) {
                        if (usuario_.getPerfil() == Perfil.AGENTE && usuario_.getCampana() == campana) {
                            JSONObject respuesta = new JSONObject();
                            respuesta.put("evento", "LOGOUT");
                            respuesta.put("usuario", usuario_logout.getUsuario());
                            respuesta.put("nombre", usuario_logout.getNombre());
                            usuario_.getSession().getAsyncRemote().sendText(respuesta.toJSONString());
                            System.out.println(new Date() + " broadcast " + usuario_.toString() + " " + respuesta.toJSONString());
                        }
                    }
                }
            }

        }
        Global.i_sessions.remove(session.getId());

    }

    @OnError
    public void onError(Throwable ex) {
        ex.printStackTrace();
    }

    public Session getSession(Long usuario) {
        for (Usuario usuario_ : Global.i_sessions.values()) {
            if (usuario_.getUsuario() == usuario) {
                return usuario_.getSession();
            }
        }
        return null;
    }

    public Usuario getContacto(Session session) {
        return Global.i_sessions.get(session.getId());
    }
}
