package com.websocket.websocket.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "Users")
public class User {

 //   String id ;
    @Id
    String name;
    TypeMessage status;
    Role role;

}
