package com.websocket.websocket.controllers;


import com.websocket.websocket.model.*;
import com.websocket.websocket.services.DocRoomService;

import com.websocket.websocket.services.EditorService;
import com.websocket.websocket.services.UserService;
import com.websocket.websocket.services.ValidationDocUser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.lang.System.*;
@Controller
@AllArgsConstructor
@Slf4j
public class ActionsController {

    private UserService userService;
    private EditorService editorService;
    private ValidationDocUser validationDocUser;

    @GetMapping("/save/{docId}/{userId}")
    public ResponseEntity<String> saveDoc(@PathVariable String docId,@PathVariable String userId) throws IOException {

        DocRoom docRoom = null;

        String temp = Editor.textArea;
        log.info("this is content from client {}",temp);

        editorService.saveDocToDb(docId,Editor.textArea);

        if (validationDocUser.userExist(userId)) {

            log.info("actionController method: user {}",userId);
            FileWriter fpWrite = new FileWriter(userId + "_" + docId + ".txt");
            fpWrite.write(temp);
            fpWrite.flush();
            fpWrite.close();

            return ResponseEntity.ok("200");
        }
        else {
            log.info("cannot save file to no user");
            return ResponseEntity.ok("-200");
        }

//        return ResponseEntity.ok("200");

    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {

        List<User> con = userService.findAllConnectedUsers();
        con.forEach(user -> log.info("findConnectedUsers: {}",user.getName()));

        return ResponseEntity.ok(userService.findAllConnectedUsers());
    }

    @PostMapping("/editor/addShareUser/{docId}/{owner}")
//    public ResponseEntity<User> addShareUser(
//                                             @RequestBody DocRoom addShareUserInDoc) {
    public ResponseEntity<Optional<User>> addShareUser(@PathVariable String docId, @PathVariable String owner,
                                            @RequestBody User addShareUserInDoc) {


        Optional<User> user = editorService.addShareUserInDoc(docId,owner,addShareUserInDoc);
//        return ResponseEntity.ok(addShareUserInDoc);
        return ResponseEntity.of(Optional.ofNullable(user));
    }

}
