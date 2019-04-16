<%-- 
    Document   : chat
    Created on : 23-oct-2018, 17:48:11
    Author     : Home
--%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Tomcat WebSocket</title>
    </head>
    <body>        
        <input id="texto" style="width: 719px" type="text">
        <textarea id="textarea" rows="5" cols="100"></textarea>
        <button id="desconectar" type="button"> Desconectar </button>
        <button id="conectar" type="button"> Conectar </button>
        <script src="js/jquery.min.js"></script>
        <script src="js/sockjs.js"></script>
        <script src="js/stomp.js"></script>
        <script src="js/app.js"></script>

    </body>
</html>