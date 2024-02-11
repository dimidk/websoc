package com.websocket.websocket.model;


import com.websocket.websocket.actions.EditActions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.servlet.View;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Builder
@Document(collection = "Doc")
public class DocRoom implements EditActions {

    @Id
    String docId;
    User ownerUser;
    List<User> shareUser;
    String content;
    List<Message> messages;

    @Override
    public void load(DocRoom textDoc) {

    }

    @Override
    public void save(DocRoom textDoc) {

    }

    @Override
    public void edit(DocRoom textDoc) {

    }


//HashMap<User,List<Message>> userMessage;


}
