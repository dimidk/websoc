package com.websocket.websocket.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.websocket.websocket.actions.EditActions;
import com.websocket.websocket.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.lang.System.err;
import static java.lang.System.exit;

@Service
@AllArgsConstructor
@Slf4j
public class EditorService  {


    private final DocRoomService docRoomService;
    private final UserService userService;

    //public void saveDocToDb(Message message) {
    public void saveDocToDb(String docId,String context) {

        DocRoom docRoom;

        if (docId == null) {
            throw new IllegalArgumentException("no name");
        }
        log.info("saveDocToDb: get docId {}",docId);

        if (docRoomService.exist(docId)) {
            docRoom = docRoomService.getDocRoom(docId);
        }

        docRoomService.updateDocument(docId,context);

    }

    public void createDoc(Message message) {

        DocRoom docRoom = null;
        List<User> shared = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        if (message == null) {
            throw new IllegalArgumentException("no message");
        }

        String docId = message.getDocId();
        log.info("createDoc method:{}",docId);
        if (!docRoomService.exist(docId)) {
            log.info("createDoc method: new Doc");
            docRoom = DocRoom.builder()
                    .docId(message.getDocId())
                    .ownerUser(userService.findUser(message.getSenderId()))
                    .shareUser(shared)
                    .content(message.getText())
                    .messages(messages)
                    .build();

            docRoomService.addDoc(docRoom);
        }
        log.info("createDoc method: existing doc");
    }

    private boolean findOwner(String docId,String name) {

        if (docId == null) {
            throw new IllegalArgumentException("no name");
        }
        if (name == null) {
            throw new IllegalArgumentException("no given status");
        }

        DocRoom docRoom = docRoomService.getDocRoom(docId);
        log.info("findOwner method: {}",docRoom.getOwnerUser().getName());
        return docRoom.getOwnerUser().getName().compareTo(name) == 0;
    }

    public String getContentDocId(String docId) {

        if (docId == null) {
            throw new IllegalArgumentException("no name");
        }

        return docRoomService.getContentDocId(docId);
    }

    public List<DocRoom> findAllDocs() {

        return docRoomService.findAllDocs();
    }


    public Optional<User> getShareUser(String docId,String userId) {
        User user = null;
        List<User> sharedUsers = null;

        if (docId == null) {
            throw new IllegalArgumentException("no name");
        }
        if (userId == null) {
            throw new IllegalArgumentException("no given status");
        }

        Optional<User>  u = docRoomService.findShareUser(docId,userId);

        return u;

    }

    public void checkDocUser(Message message) {

        List<User> shared = null;
        Optional<User> userFind = null;

        if (message == null) {
            throw new IllegalArgumentException("noe message");
        }

        DocRoom docRoom = docRoomService.getDocRoom(message.getDocId());
        shared = docRoom.getShareUser();
        User user = userService.findUser(message.getSenderId());
        userFind = docRoomService.findShareUser(message.getDocId(), user.getName());
        if ((shared.isEmpty() && !findOwner(docRoom.getDocId(),user.getName())) ||
            userFind.isEmpty()) {

            log.info("checkDocUser method: user share no exist and not owner");
            shared = docRoom.getShareUser();
            shared.add(user);
            docRoomService.addDoc(docRoom);
            //throw new IllegalArgumentException("there is new created doc with no share users");
        }


    }

    public void userLoginUpdate(Message message, Role roleType) {

        if (message == null) {
            throw new IllegalArgumentException("noe message");
        }

        try {

            if (!userService.existsByName(message.getSenderId())) {
                //if (!userService.exists(loginUser)) {
                log.info("login user doesn't exist");
                User loginUser = User.builder()
                        .name(message.getSenderId())
                        .role(roleType)
                        .status(message.getType())
                        .build();

                userService.addNew(loginUser);
            }
            userService.updateUser(message.getSenderId(),message.getType());
        } catch (Exception e) {
            log.info("error in User id {}",e.getMessage());
            //throw new RuntimeException(e);
        }

    }

    public void userDisconnectUpdate(Message message) {

        if (message == null) {
            throw new IllegalArgumentException("noe message");
        }

        if (message.getType() == TypeMessage.LEAVE) {
            userService.updateUser(message.getSenderId(), TypeMessage.LEAVE);
        }
    }
}
