package com.websocket.websocket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    TypeMessage status;

    @JsonProperty
    Role role;

    @JsonCreator
    public static User of (@JsonProperty("name") String name,
                           @JsonProperty("status") String  status,
                           @JsonProperty("role") String role) {

        User user = new User();
        user.name = name;

        TypeMessage statusObj = TypeMessage.valueOf(status);
        Role roleObj = Role.valueOf(Role.class,role);

        user.status = statusObj;
        user.role = roleObj;
        
        return user;
    }

}
