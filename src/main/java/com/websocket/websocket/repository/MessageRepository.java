package com.websocket.websocket.repository;

import com.websocket.websocket.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message,String> {
}
