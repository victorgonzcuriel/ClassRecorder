package com.victorgonzcuriel.classrecorder.websocket;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.victorgonzcuriel.classrecorder.classes.WebSocketMsg;

@Controller
public class WebSocketHandler {

	@MessageMapping("/msg")
	@SendTo("/subscription/sub")
	public WebSocketMsg send(WebSocketMsg message) throws Exception {
		Robot robot = new Robot();
		
		if(message.getAction().equals("LEFT")) {
			robot.keyPress(KeyEvent.VK_LEFT);
			robot.keyRelease(KeyEvent.VK_LEFT);
		}else if(message.getAction().equals("RIGHT")) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			robot.keyRelease(KeyEvent.VK_RIGHT);
		}else if(message.getAction().equals("PLAY")){
			robot.keyPress(KeyEvent.VK_P);
			robot.keyRelease(KeyEvent.VK_P);
		}else if(message.getAction().equals("STOP")) {
			robot.keyPress(KeyEvent.VK_R);
			robot.keyRelease(KeyEvent.VK_R);
		//tanto para un nuevo fichero o bajarme un fichero, paso el mensaje al móvil
		//si es para borrar un fichero o que ha terminado la clase tambien se lo paso al móvil
		}else if(message.getAction().equals("NEW") || message.getAction().equals("DOWNLOAD") 
				|| message.getAction().equals("DELETE") || message.getAction().equals("CLASSENDED"))
			return message;
		//si es que se ha recibido un fichero, mando mensaje para recargar pagina
		else if(message.getAction().equals("SENDED"))
			return new WebSocketMsg("RELOAD");
		//si se ha recibido que se ha borrado un fichero, mando el mensaje para borrar fichero
		else if(message.getAction().equals("DELETED"))
			return new WebSocketMsg("DELETED");
		//si esta comprobando la conexión le devuelvo un 
	    return new WebSocketMsg("OK");
	}
}
