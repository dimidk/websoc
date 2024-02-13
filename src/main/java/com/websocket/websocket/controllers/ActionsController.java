package com.websocket.websocket.controllers;


import com.websocket.websocket.model.*;
import com.websocket.websocket.services.DocRoomService;

import com.websocket.websocket.services.EditorService;
import com.websocket.websocket.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.io.*;
import java.util.List;
import java.util.Optional;

import static java.lang.System.*;
@Controller
@AllArgsConstructor
@Slf4j
public class ActionsController {

    private UserService userService;
    private EditorService editorService;

    @GetMapping("/save/{docId}/{userId}")
    public ResponseEntity<String> saveDoc(@PathVariable String docId,@PathVariable String userId) throws IOException {

        DocRoom docRoom = null;

        String temp = Editor.textArea;
        log.info("this is content from client {}",temp);

        editorService.saveDocToDb(docId,Editor.textArea);
        Optional<User> user = editorService.getShareUser(docId,userId);

        if (user.isEmpty()) {
            log.info("cannot save file without name");
            return ResponseEntity.ok("Null user");
        }
        else {
            log.info("ActionsController get user to save file {}", user.get().getName());
            FileWriter fpWrite = new FileWriter(user.get().getName() + "_" + docId + ".txt");
            fpWrite.write(temp);
            fpWrite.flush();
            fpWrite.close();
        }

        return ResponseEntity.ok("200");

    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {

        List<User> con = userService.findAllConnectedUsers();
        con.forEach(user -> log.info("findConnectedUsers: {}",user.getName()));

        return ResponseEntity.ok(userService.findAllConnectedUsers());
    }

    @PostMapping("/editor/addShareUser")
    public ResponseEntity<User> addShareUser(@RequestBody User shareUser) {

        User shareUserNew = null;

        if (shareUser == null) {
            throw new IllegalArgumentException("no user added");
        }

        if (!userService.exists(shareUser)) {

            shareUserNew = User.builder()
                    .name(shareUser.getName())
                    .status(TypeMessage.JOIN)
                    .role(shareUser.getRole())
                    .build();

            userService.addNew(shareUserNew);
        }

        return ResponseEntity.ok(shareUserNew);

    }



}
