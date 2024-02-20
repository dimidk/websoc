package com.websocket.websocket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.websocket.websocket.model.TypeMessage;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "Message")
public class Message {

    @Id
    String id;
    String text;
    String senderId;
    String docId;
    TypeMessage type;
}
