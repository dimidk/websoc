package com.websocket.websocket.controllers;

import com.websocket.websocket.model.*;
import com.websocket.websocket.services.DocRoomService;
import com.websocket.websocket.services.EditorService;
import com.websocket.websocket.services.MessageService;
import com.websocket.websocket.services.UserService;
import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Controller
@AllArgsConstructor
@Slf4j
public class Editor {

    private final UserService userService;
    private final DocRoomService docRoomService;
    private final EditorService editorService;
    protected static String textArea = null;
    private static int id = 0;


    @MessageMapping("/controllers.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message) {

      log.info("This is message {}",message.getText());

        Editor.textArea = message.getText();

        //DocRoom docRoom = docRoomService.getDocRoom(message.getDocId());
        //docRoomService.updateDocument(docRoom.getDocId(),Editor.textArea);

        log.info("this is textArea content {}",textArea);

        //docRoomService.updateDocument(message.getDocId(), textArea);
        editorService.saveDocToDb(message.getDocId(),Editor.textArea);
      return message;
    }

    @MessageMapping("/controllers.subscribeToView")
    public void viewOnlyMessage(@Payload Message message) {

        log.info("This is a subscription only user {}",message.toString());

        editorService.userLoginUpdate(message, Role.VIEW);
        editorService.checkDocUser(message);


    }

    @MessageMapping("/controllers.docRoomUser")
    @SendTo("/topic/public")
    public Message logUserToDoc(@Payload Message message, @RequestParam String username) {

        Editor.id += 1;

        log.info("first message {}",message.getText());
        log.info("user logged in {}",username);

        log.info("check if user exits");
        editorService.userLoginUpdate(message,Role.EDIT);
        editorService.createDoc(message);
        editorService.checkDocUser(message);
        //editorService.saveDocToDb(message.getDocId(),message.getText());

        if (textArea == null && editorService.findAllDocs().isEmpty()) {
            Editor.textArea = message.getText();
        }

        //Στην προηγούμενη έκδοση το έχω uncomment αυτό για να αποφύγω στο disconnection την διαγραφή απο
        //τον editor του content
//        χρειάζεται για το υπάρχων document τελικά γι'αυτό αυτή η γραμμή
        Editor.textArea = editorService.getContentDocId(message.getDocId());
        message.setText(Editor.textArea);
        editorService.saveDocToDb(message.getDocId(),message.getText());

        log.info("this is first time of user logged textArea {}",textArea);

        return message;
    }

    @MessageMapping("controllers.onDisconnect")
    @SendTo("/topic/public")
    public Message onDisconnect(@Payload Message message, StompHeaderAccessor headerAccessor) {


        if (headerAccessor.getCommand().equals(StompCommand.CONNECT )) {

            headerAccessor.setMessageTypeIfNotSet(SimpMessageType.DISCONNECT);
        }

        editorService.userDisconnectUpdate(message);

        return message;
    }

}
