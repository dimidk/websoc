package com.websocket.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
basic function of collaboration
writes Users to mongo
creates a TextDoc but not write the context in it
and a bug: when a user logs in after many messages written everything is deleted
 */

@SpringBootApplication
public class WebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketApplication.class, args);
	}

}
