package com.websocket.websocket.services;

import com.websocket.websocket.model.Message;
import com.websocket.websocket.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class ValidationDocUser {

    private final DocRoomService docRoomService;
    private final UserService userService;


    public boolean docExist(Message message) {

//        boolean a = true;

        String docId = message.getDocId();
        return docRoomService.exist(docId);
    }

    public boolean userExist(Message message) {

        String name = message.getSenderId();
        return userService.existsByName(name);
    }

    public boolean userExist(String name) {

        return userService.existsByName(name);

    }

    private void addNewUser(User name) {

        if (name == null) {
            throw new IllegalArgumentException("null object");
        }

        try {
            if (!userService.exists(name)) {

                userService.addNew(name);
            }
        }catch (Exception e) {
            log.info("cannot add same user: {}",e.getMessage());
        }
    }

}
