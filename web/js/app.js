var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('http://192.168.10.54:8084/mail/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        //console.log('Connected: ' + frame);
        stompClient.subscribe('/controlmensajes/mensajes', function (respuesta) {
//            escribir(JSON.parse(respuesta.body).texto);
            escribir(JSON.parse(respuesta.body));
        });
    });
}


$('#desconectar').click(function(){
    disconnect();
});
$('#conectar').click(function(){
    connect();
});


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function enviarMensaje() {
    stompClient.send("/mensajes/prueba", {}, JSON.stringify({'texto': $("#texto").val()}));
}

function escribir(respuesta) {    
//    $("#textarea").val($("#textarea").val() + "\n[" + respuesta.from + "] " + respuesta.event + " > " + respuesta.text);
$("#textarea").val($("#textarea").val() + "\n"+respuesta.texto);
    document.getElementById("textarea").scrollTop = document.getElementById("textarea").scrollHeight;
    $("#texto").val('');
}

$(function () {
//    $("form").on('submit', function (e) {
//        e.preventDefault();
//    });
//    $( "#connect" ).click(function() { connect(); });
//    $( "#disconnect" ).click(function() { disconnect(); });
//    $( "#send" ).click(function() { sendName(); });

    $('#texto').on('keypress', function (e) {
        if (e.which === 13) {
            enviarMensaje();
        }
    });

});

$(document).ready(function () {
    connect();
});
